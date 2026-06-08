package com.shanxi.shopping.service;

import com.shanxi.shopping.model.vo.OrderVO;
import com.shanxi.shopping.model.vo.PageVO;

import java.util.List;

/**
 * 订单服务接口
 */
public interface OrderService {

    /** 创建订单（从购物车勾选项） */
    OrderVO createOrder(Long userId, Long addressId, List<Long> cartItemIds);

    /** 买家订单列表 */
    PageVO<OrderVO> getUserOrders(Long userId, Integer status, int page, int pageSize);

    /** 订单详情 */
    OrderVO getOrderDetail(Long orderId);

    /** 买家取消订单（仅待付款状态可取消） */
    void cancelOrder(Long userId, Long orderId);

    /** 买家确认收货 */
    void confirmReceive(Long userId, Long orderId);

    /** 买家申请退货 */
    void requestReturn(Long userId, Long orderId, String reason);

    /** 模拟支付 */
    void pay(Long userId, Long orderId);

    /** ---- 卖家操作 ---- */

    /** 卖家订单列表 */
    PageVO<OrderVO> getSellerOrders(Long sellerId, Integer status, int page, int pageSize);

    /** 卖家发货 */
    void shipOrder(Long sellerId, Long orderId, String logisticsCompany, String logisticsNo);

    /** 卖家处理退货 */
    void processReturn(Long sellerId, Long returnId, boolean approved, String remark);
}
