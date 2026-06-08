package com.shanxi.shopping.model.vo;

import java.math.BigDecimal;

/**
 * 购物车列表 VO（含商品信息）
 */
public class CartVO {
    private Long id;
    private Long productId;
    private String productName;
    private String productImage;
    private BigDecimal price;
    private Integer stock;
    private Integer quantity;
    private Integer checked;
    private Boolean stockEnough;    // 库存是否充足

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getProductImage() { return productImage; }
    public void setProductImage(String productImage) { this.productImage = productImage; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public Integer getChecked() { return checked; }
    public void setChecked(Integer checked) { this.checked = checked; }
    public Boolean getStockEnough() { return stockEnough; }
    public void setStockEnough(Boolean stockEnough) { this.stockEnough = stockEnough; }
}
