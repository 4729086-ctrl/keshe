package com.shanxi.shopping.service.impl;

import com.shanxi.shopping.common.BusinessException;
import com.shanxi.shopping.mapper.UserAddressMapper;
import com.shanxi.shopping.mapper.UserMapper;
import com.shanxi.shopping.model.dto.AddressDTO;
import com.shanxi.shopping.model.dto.LoginDTO;
import com.shanxi.shopping.model.dto.RegisterDTO;
import com.shanxi.shopping.model.entity.User;
import com.shanxi.shopping.model.entity.UserAddress;
import com.shanxi.shopping.model.vo.LoginVO;
import com.shanxi.shopping.security.JwtUtil;
import com.shanxi.shopping.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserAddressMapper addressMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserServiceImpl(UserMapper userMapper, UserAddressMapper addressMapper,
                           PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.addressMapper = addressMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    @Transactional
    public User register(RegisterDTO dto) {
        if (userMapper.findByUsername(dto.getUsername()) != null) {
            throw new BusinessException("用户名已存在");
        }
        if (dto.getEmail() != null && userMapper.findByEmail(dto.getEmail()) != null) {
            throw new BusinessException("邮箱已被注册");
        }
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        userMapper.insert(user);
        return user;
    }

    @Override
    public LoginVO login(LoginDTO dto) {
        User user = userMapper.findByUsername(dto.getUsername());
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        userMapper.updateLoginTime(user.getId());

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), "USER");
        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRole("USER");
        return vo;
    }

    @Override
    public User getUserById(Long id) {
        User user = userMapper.findById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setPassword(null);  // 不暴露密码
        return user;
    }

    @Override
    public void updateUser(User user) {
        userMapper.update(user);
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userMapper.findById(userId);
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        userMapper.updatePassword(userId, passwordEncoder.encode(newPassword));
    }

    // ---- 收货地址 ----

    @Override
    public List<UserAddress> getAddresses(Long userId) {
        return addressMapper.findByUserId(userId);
    }

    @Override
    public UserAddress getAddressById(Long id) {
        UserAddress addr = addressMapper.findById(id);
        if (addr == null) {
            throw new BusinessException("地址不存在");
        }
        return addr;
    }

    @Override
    @Transactional
    public UserAddress addAddress(Long userId, AddressDTO dto) {
        UserAddress addr = new UserAddress();
        addr.setUserId(userId);
        addr.setReceiverName(dto.getReceiverName());
        addr.setReceiverPhone(dto.getReceiverPhone());
        addr.setProvince(dto.getProvince());
        addr.setCity(dto.getCity());
        addr.setDistrict(dto.getDistrict());
        addr.setDetail(dto.getDetail());
        addr.setIsDefault(dto.getIsDefault());

        if (dto.getIsDefault() == 1) {
            addressMapper.clearDefault(userId);
        }
        addressMapper.insert(addr);
        return addr;
    }

    @Override
    public void updateAddress(Long id, AddressDTO dto) {
        UserAddress addr = addressMapper.findById(id);
        if (addr == null) {
            throw new BusinessException("地址不存在");
        }
        addr.setReceiverName(dto.getReceiverName());
        addr.setReceiverPhone(dto.getReceiverPhone());
        addr.setProvince(dto.getProvince());
        addr.setCity(dto.getCity());
        addr.setDistrict(dto.getDistrict());
        addr.setDetail(dto.getDetail());
        addr.setIsDefault(dto.getIsDefault());
        if (dto.getIsDefault() == 1) {
            addressMapper.clearDefault(addr.getUserId());
        }
        addressMapper.update(addr);
    }

    @Override
    public void deleteAddress(Long id) {
        addressMapper.deleteById(id);
    }

    @Override
    public void setDefaultAddress(Long id, Long userId) {
        addressMapper.clearDefault(userId);
        addressMapper.setDefault(id);
    }
}
