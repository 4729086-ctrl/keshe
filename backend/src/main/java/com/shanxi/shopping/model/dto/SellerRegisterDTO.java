package com.shanxi.shopping.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 卖家注册请求 DTO
 */
public class SellerRegisterDTO {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 50)
    private String password;

    @NotBlank(message = "店铺名称不能为空")
    private String shopName;

    @NotBlank(message = "手机号不能为空")
    private String phone;

    private String email;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getShopName() { return shopName; }
    public void setShopName(String shopName) { this.shopName = shopName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
