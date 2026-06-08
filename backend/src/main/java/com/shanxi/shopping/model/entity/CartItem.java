package com.shanxi.shopping.model.entity;

import java.time.LocalDateTime;

/**
 * 购物车项实体 — 对应 cart_items 表
 */
public class CartItem {
    private Long id;
    private Long userId;
    private Long productId;
    private Integer quantity;
    private Integer checked;      // 1-选中, 0-未选中
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public Integer getChecked() { return checked; }
    public void setChecked(Integer checked) { this.checked = checked; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
