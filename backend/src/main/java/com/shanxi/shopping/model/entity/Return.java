package com.shanxi.shopping.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 退货记录实体 — 对应 returns 表
 */
public class Return {
    private Long id;
    private Long orderId;
    private Long userId;
    private String reason;
    private Integer status;       // 1-待处理, 2-已同意, 3-已拒绝, 4-已完成
    private BigDecimal refundAmount;
    private String sellerRemark;
    private LocalDateTime processedAt;
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public BigDecimal getRefundAmount() { return refundAmount; }
    public void setRefundAmount(BigDecimal refundAmount) { this.refundAmount = refundAmount; }
    public String getSellerRemark() { return sellerRemark; }
    public void setSellerRemark(String sellerRemark) { this.sellerRemark = sellerRemark; }
    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
