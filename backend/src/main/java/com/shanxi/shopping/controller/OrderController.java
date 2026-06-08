package com.shanxi.shopping.controller;

import com.shanxi.shopping.common.Result;
import com.shanxi.shopping.model.dto.CreateOrderDTO;
import com.shanxi.shopping.model.vo.OrderVO;
import com.shanxi.shopping.model.vo.PageVO;
import com.shanxi.shopping.security.SecurityUtil;
import com.shanxi.shopping.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 订单 Controller（买家端）
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /** 创建订单 */
    @PostMapping
    public Result<OrderVO> create(@Valid @RequestBody CreateOrderDTO dto) {
        Long userId = SecurityUtil.getCurrentUserId();
        return Result.ok(orderService.createOrder(userId, dto.getAddressId(), dto.getCartItemIds()));
    }

    /** 我的订单列表 */
    @GetMapping
    public Result<PageVO<OrderVO>> list(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        Long userId = SecurityUtil.getCurrentUserId();
        return Result.ok(orderService.getUserOrders(userId, status, page, pageSize));
    }

    /** 订单详情 */
    @GetMapping("/{id}")
    public Result<OrderVO> detail(@PathVariable Long id) {
        return Result.ok(orderService.getOrderDetail(id));
    }

    /** 模拟支付 */
    @PutMapping("/{id}/pay")
    public Result<?> pay(@PathVariable Long id) {
        Long userId = SecurityUtil.getCurrentUserId();
        orderService.pay(userId, id);
        return Result.ok();
    }

    /** 取消订单 */
    @PutMapping("/{id}/cancel")
    public Result<?> cancel(@PathVariable Long id) {
        Long userId = SecurityUtil.getCurrentUserId();
        orderService.cancelOrder(userId, id);
        return Result.ok();
    }

    /** 确认收货 */
    @PutMapping("/{id}/receive")
    public Result<?> receive(@PathVariable Long id) {
        Long userId = SecurityUtil.getCurrentUserId();
        orderService.confirmReceive(userId, id);
        return Result.ok();
    }

    /** 申请退货 */
    @PostMapping("/{id}/return")
    public Result<?> requestReturn(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Long userId = SecurityUtil.getCurrentUserId();
        orderService.requestReturn(userId, id, body.get("reason"));
        return Result.ok();
    }
}
