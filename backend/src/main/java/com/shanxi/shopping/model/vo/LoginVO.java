package com.shanxi.shopping.model.vo;

/**
 * 登录响应 VO
 */
public class LoginVO {
    private String token;
    private Long userId;
    private String username;
    private String role;          // USER / SELLER
    private String shopName;     // 仅卖家有值

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getShopName() { return shopName; }
    public void setShopName(String shopName) { this.shopName = shopName; }
}
