package com.shanxi.shopping.service;

import com.shanxi.shopping.model.dto.AddressDTO;
import com.shanxi.shopping.model.dto.LoginDTO;
import com.shanxi.shopping.model.dto.RegisterDTO;
import com.shanxi.shopping.model.entity.User;
import com.shanxi.shopping.model.entity.UserAddress;
import com.shanxi.shopping.model.vo.LoginVO;

import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService {

    /** 用户注册 */
    User register(RegisterDTO dto);

    /** 用户登录 */
    LoginVO login(LoginDTO dto);

    /** 获取用户信息 */
    User getUserById(Long id);

    /** 更新用户信息 */
    void updateUser(User user);

    /** 修改密码 */
    void changePassword(Long userId, String oldPassword, String newPassword);

    /** ---- 收货地址 ---- */

    List<UserAddress> getAddresses(Long userId);

    UserAddress getAddressById(Long id);

    UserAddress addAddress(Long userId, AddressDTO dto);

    void updateAddress(Long id, AddressDTO dto);

    void deleteAddress(Long id);

    void setDefaultAddress(Long id, Long userId);
}
