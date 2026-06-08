package com.shanxi.shopping.service.impl;

import com.shanxi.shopping.common.BusinessException;
import com.shanxi.shopping.common.OrderStatus;
import com.shanxi.shopping.common.ReturnStatus;
import com.shanxi.shopping.mapper.*;
import com.shanxi.shopping.model.entity.*;
import com.shanxi.shopping.model.vo.OrderItemVO;
import com.shanxi.shopping.model.vo.OrderVO;
import com.shanxi.shopping.model.vo.PageVO;
import com.shanxi.shopping.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final CartItemMapper cartItemMapper;
    private final ProductMapper productMapper;
    private final UserAddressMapper addressMapper;
    private final ReturnMapper returnMapper;

    public OrderServiceImpl(OrderMapper orderMapper, OrderItemMapper orderItemMapper,
                            CartItemMapper cartItemMapper, ProductMapper productMapper,
                            UserAddressMapper addressMapper, ReturnMapper returnMapper) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.cartItemMapper = cartItemMapper;
        this.productMapper = productMapper;
        this.addressMapper = addressMapper;
        this.returnMapper = returnMapper;
    }

    @Override
    @Transactional
    public OrderVO createOrder(Long userId, Long addressId, List<Long> cartItemIds) {
        // 1. 获取收货地址快照
        UserAddress address = addressMapper.findById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException("收货地址不存在");
        }

        // 2. 获取勾选的购物车项
        List<CartItem> checkedItems = cartItemMapper.findCheckedByUserId(userId);
        if (checkedItems.isEmpty()) {
            throw new BusinessException("请先勾选要购买的商品");
        }

        // 筛选出指定的购物车项
        List<CartItem> selected = checkedItems.stream()
                .filter(c -> cartItemIds.contains(c.getId()))
                .toList();
        if (selected.isEmpty()) {
            throw new BusinessException("没有选中的商品");
        }

        // 3. 按卖家分组（一个卖家一个订单）
        // 简化处理：所有选中商品需属于同一卖家，或拆分为多个订单
        // 此处按第一个商品的卖家为准（简化版）
        Product firstProduct = productMapper.findById(selected.get(0).getProductId());
        Long sellerId = firstProduct.getSellerId();

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem cartItem : selected) {
            Product product = productMapper.findById(cartItem.getProductId());
            if (product == null || product.getStatus() != 1) {
                throw new BusinessException("商品【" + product.getName() + "】已下架");
            }
            if (!product.getSellerId().equals(sellerId)) {
                throw new BusinessException("请按卖家分开下单");
            }

            // 扣减库存（行级锁，防止超卖）
            int affected = productMapper.deductStock(product.getId(), cartItem.getQuantity());
            if (affected == 0) {
                throw new BusinessException("商品【" + product.getName() + "】库存不足");
            }

            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalAmount = totalAmount.add(subtotal);

            OrderItem item = new OrderItem();
            item.setProductId(product.getId());
            item.setProductName(product.getName());
            item.setProductImage(product.getMainImage());
            item.setPrice(product.getPrice());
            item.setQuantity(cartItem.getQuantity());
            item.setSubtotal(subtotal);
            orderItems.add(item);
        }

        // 4. 创建订单
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setSellerId(sellerId);
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.PENDING_PAY.getCode());
        order.setReceiverName(address.getReceiverName());
        order.setReceiverPhone(address.getReceiverPhone());
        order.setReceiverAddress(
                address.getProvince() + address.getCity() + address.getDistrict() + " " + address.getDetail()
        );
        orderMapper.insert(order);

        // 5. 保存订单明细
        for (OrderItem item : orderItems) {
            item.setOrderId(order.getId());
        }
        orderItemMapper.insertBatch(orderItems);

        // 6. 清空已下单的购物车项
        cartItemMapper.deleteByIds(selected.stream().map(CartItem::getId).toList());

        // 7. 组装返回
        return buildOrderVO(order, orderItems);
    }

    @Override
    public PageVO<OrderVO> getUserOrders(Long userId, Integer status, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<OrderVO> list = orderMapper.findUserOrders(userId, status, offset, pageSize);
        long total = orderMapper.countUserOrders(userId, status);
        // 填充状态描述和订单明细
        for (OrderVO vo : list) {
            vo.setStatusDesc(getStatusDesc(vo.getStatus()));
            List<OrderItem> items = orderItemMapper.findByOrderId(vo.getId());
            vo.setItems(items.stream().map(this::toItemVO).toList());
        }
        return new PageVO<>(list, total, page, pageSize);
    }

    @Override
    public OrderVO getOrderDetail(Long orderId) {
        OrderVO vo = orderMapper.findOrderDetail(orderId);
        if (vo == null) {
            throw new BusinessException(404, "订单不存在");
        }
        vo.setStatusDesc(getStatusDesc(vo.getStatus()));
        List<OrderItem> items = orderItemMapper.findByOrderId(orderId);
        vo.setItems(items.stream().map(this::toItemVO).toList());
        return vo;
    }

    @Override
    @Transactional
    public void cancelOrder(Long userId, Long orderId) {
        Order order = orderMapper.findById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在");
        }
        if (!OrderStatus.canCancel(order.getStatus())) {
            throw new BusinessException("当前状态不可取消");
        }
        orderMapper.updateStatus(orderId, order.getStatus(), OrderStatus.CANCELLED.getCode());

        // 回补库存
        List<OrderItem> items = orderItemMapper.findByOrderId(orderId);
        for (OrderItem item : items) {
            productMapper.addStock(item.getProductId(), item.getQuantity());
        }
    }

    @Override
    public void confirmReceive(Long userId, Long orderId) {
        Order order = orderMapper.findById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在");
        }
        if (!OrderStatus.canReceive(order.getStatus())) {
            throw new BusinessException("当前状态不可确认收货");
        }
        orderMapper.markReceived(orderId, order.getStatus(), OrderStatus.COMPLETED.getCode());
    }

    @Override
    @Transactional
    public void requestReturn(Long userId, Long orderId, String reason) {
        Order order = orderMapper.findById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在");
        }
        if (!OrderStatus.canReturn(order.getStatus())) {
            throw new BusinessException("当前状态不可申请退货");
        }
        // 检查是否已有退货记录
        Return existing = returnMapper.findByOrderId(orderId);
        if (existing != null) {
            throw new BusinessException("已申请过退货，请勿重复申请");
        }

        Return r = new Return();
        r.setOrderId(orderId);
        r.setUserId(userId);
        r.setReason(reason);
        r.setStatus(ReturnStatus.PENDING.getCode());
        r.setRefundAmount(order.getTotalAmount());
        returnMapper.insert(r);

        // 更新订单状态为退货中
        orderMapper.updateStatus(orderId, order.getStatus(), OrderStatus.RETURNING.getCode());
    }

    @Override
    @Transactional
    public void pay(Long userId, Long orderId) {
        Order order = orderMapper.findById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != OrderStatus.PENDING_PAY.getCode()) {
            throw new BusinessException("订单状态异常");
        }
        // 模拟支付：直接修改状态为待发货
        orderMapper.updateStatus(orderId, OrderStatus.PENDING_PAY.getCode(), OrderStatus.PENDING_SHIP.getCode());
    }

    // ---- 卖家操作 ----

    @Override
    public PageVO<OrderVO> getSellerOrders(Long sellerId, Integer status, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<OrderVO> list = orderMapper.findSellerOrders(sellerId, status, offset, pageSize);
        long total = orderMapper.countSellerOrders(sellerId, status);
        for (OrderVO vo : list) {
            vo.setStatusDesc(getStatusDesc(vo.getStatus()));
        }
        return new PageVO<>(list, total, page, pageSize);
    }

    @Override
    public void shipOrder(Long sellerId, Long orderId, String logisticsCompany, String logisticsNo) {
        Order order = orderMapper.findById(orderId);
        if (order == null || !order.getSellerId().equals(sellerId)) {
            throw new BusinessException("订单不存在");
        }
        if (!OrderStatus.canShip(order.getStatus())) {
            throw new BusinessException("当前状态不可发货");
        }
        orderMapper.ship(orderId, order.getStatus(), OrderStatus.PENDING_RECEIVE.getCode(),
                logisticsCompany, logisticsNo);
    }

    @Override
    @Transactional
    public void processReturn(Long sellerId, Long returnId, boolean approved, String remark) {
        Return r = returnMapper.findById(returnId);
        if (r == null) {
            throw new BusinessException("退货申请不存在");
        }
        if (r.getStatus() != ReturnStatus.PENDING.getCode()) {
            throw new BusinessException("退货申请已处理");
        }
        // 验证退货单属于该卖家的订单
        Order order = orderMapper.findById(r.getOrderId());
        if (order == null || !order.getSellerId().equals(sellerId)) {
            throw new BusinessException("无权处理此退货申请");
        }

        if (approved) {
            r.setStatus(ReturnStatus.APPROVED.getCode());
            // 回补库存
            List<OrderItem> items = orderItemMapper.findByOrderId(order.getId());
            for (OrderItem item : items) {
                productMapper.addStock(item.getProductId(), item.getQuantity());
            }
            // 更新订单状态为已退货
            orderMapper.updateStatus(order.getId(), order.getStatus(), OrderStatus.RETURNED.getCode());
        } else {
            r.setStatus(ReturnStatus.REJECTED.getCode());
            // 恢复订单原状态
            orderMapper.updateStatus(order.getId(), order.getStatus(), OrderStatus.COMPLETED.getCode());
        }
        r.setSellerRemark(remark);
        returnMapper.process(returnId, r.getStatus(), remark);
    }

    // ---- 辅助方法 ----

    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return "SX" + timestamp + uuid;
    }

    private String getStatusDesc(int status) {
        for (OrderStatus os : OrderStatus.values()) {
            if (os.getCode() == status) return os.getDesc();
        }
        return "未知";
    }

    private OrderItemVO toItemVO(OrderItem item) {
        OrderItemVO vo = new OrderItemVO();
        vo.setId(item.getId());
        vo.setProductId(item.getProductId());
        vo.setProductName(item.getProductName());
        vo.setProductImage(item.getProductImage());
        vo.setPrice(item.getPrice());
        vo.setQuantity(item.getQuantity());
        vo.setSubtotal(item.getSubtotal());
        return vo;
    }

    private OrderVO buildOrderVO(Order order, List<OrderItem> items) {
        OrderVO vo = new OrderVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setUserId(order.getUserId());
        vo.setSellerId(order.getSellerId());
        vo.setTotalAmount(order.getTotalAmount());
        vo.setStatus(order.getStatus());
        vo.setStatusDesc(getStatusDesc(order.getStatus()));
        vo.setReceiverName(order.getReceiverName());
        vo.setReceiverPhone(order.getReceiverPhone());
        vo.setReceiverAddress(order.getReceiverAddress());
        vo.setCreatedAt(order.getCreatedAt());
        vo.setItems(items.stream().map(this::toItemVO).toList());
        return vo;
    }
}
