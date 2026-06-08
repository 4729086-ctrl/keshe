package com.shanxi.shopping.model.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 创建订单请求 DTO
 */
public class CreateOrderDTO {

    @NotNull(message = "收货地址ID不能为空")
    private Long addressId;

    @NotNull(message = "订单商品不能为空")
    private List<Long> cartItemIds;   // 从购物车勾选的项来下单

    private String remark;            // 订单备注

    public Long getAddressId() { return addressId; }
    public void setAddressId(Long addressId) { this.addressId = addressId; }
    public List<Long> getCartItemIds() { return cartItemIds; }
    public void setCartItemIds(List<Long> cartItemIds) { this.cartItemIds = cartItemIds; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
