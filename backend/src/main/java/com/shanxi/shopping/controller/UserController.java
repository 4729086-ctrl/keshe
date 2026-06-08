package com.shanxi.shopping.controller;

import com.shanxi.shopping.common.Result;
import com.shanxi.shopping.model.dto.AddressDTO;
import com.shanxi.shopping.model.dto.LoginDTO;
import com.shanxi.shopping.model.dto.RegisterDTO;
import com.shanxi.shopping.model.entity.User;
import com.shanxi.shopping.model.entity.UserAddress;
import com.shanxi.shopping.security.SecurityUtil;
import com.shanxi.shopping.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 用户（买家）Controller
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /** 注册 */
    @PostMapping("/register")
    public Result<?> register(@Valid @RequestBody RegisterDTO dto) {
        userService.register(dto);
        return Result.ok();
    }

    /** 登录 */
    @PostMapping("/login")
    public Result<?> login(@Valid @RequestBody LoginDTO dto) {
        return Result.ok(userService.login(dto));
    }

    /** 获取个人信息 */
    @GetMapping("/profile")
    public Result<User> profile() {
        Long userId = SecurityUtil.getCurrentUserId();
        return Result.ok(userService.getUserById(userId));
    }

    /** 修改个人信息 */
    @PutMapping("/profile")
    public Result<?> updateProfile(@RequestBody User user) {
        Long userId = SecurityUtil.getCurrentUserId();
        user.setId(userId);
        userService.updateUser(user);
        return Result.ok();
    }

    /** 修改密码 */
    @PutMapping("/password")
    public Result<?> changePassword(@RequestBody Map<String, String> params) {
        Long userId = SecurityUtil.getCurrentUserId();
        userService.changePassword(userId, params.get("oldPassword"), params.get("newPassword"));
        return Result.ok();
    }

    // ---- 收货地址 ----

    /** 地址列表 */
    @GetMapping("/addresses")
    public Result<List<UserAddress>> addresses() {
        Long userId = SecurityUtil.getCurrentUserId();
        return Result.ok(userService.getAddresses(userId));
    }

    /** 地址详情 */
    @GetMapping("/addresses/{id}")
    public Result<UserAddress> getAddress(@PathVariable Long id) {
        return Result.ok(userService.getAddressById(id));
    }

    /** 新增地址 */
    @PostMapping("/addresses")
    public Result<UserAddress> addAddress(@Valid @RequestBody AddressDTO dto) {
        Long userId = SecurityUtil.getCurrentUserId();
        return Result.ok(userService.addAddress(userId, dto));
    }

    /** 编辑地址 */
    @PutMapping("/addresses/{id}")
    public Result<?> updateAddress(@PathVariable Long id, @Valid @RequestBody AddressDTO dto) {
        userService.updateAddress(id, dto);
        return Result.ok();
    }

    /** 删除地址 */
    @DeleteMapping("/addresses/{id}")
    public Result<?> deleteAddress(@PathVariable Long id) {
        userService.deleteAddress(id);
        return Result.ok();
    }

    /** 设为默认地址 */
    @PutMapping("/addresses/{id}/default")
    public Result<?> setDefault(@PathVariable Long id) {
        Long userId = SecurityUtil.getCurrentUserId();
        userService.setDefaultAddress(id, userId);
        return Result.ok();
    }
}
