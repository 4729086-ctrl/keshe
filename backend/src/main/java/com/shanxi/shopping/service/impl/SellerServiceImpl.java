package com.shanxi.shopping.service.impl;

import com.shanxi.shopping.common.BusinessException;
import com.shanxi.shopping.mapper.OrderMapper;
import com.shanxi.shopping.mapper.ReturnMapper;
import com.shanxi.shopping.mapper.SellerMapper;
import com.shanxi.shopping.model.dto.LoginDTO;
import com.shanxi.shopping.model.dto.SellerRegisterDTO;
import com.shanxi.shopping.model.entity.Return;
import com.shanxi.shopping.model.entity.Seller;
import com.shanxi.shopping.model.vo.LoginVO;
import com.shanxi.shopping.security.JwtUtil;
import com.shanxi.shopping.service.SellerService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SellerServiceImpl implements SellerService {

    private final SellerMapper sellerMapper;
    private final OrderMapper orderMapper;
    private final ReturnMapper returnMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public SellerServiceImpl(SellerMapper sellerMapper, OrderMapper orderMapper,
                             ReturnMapper returnMapper, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.sellerMapper = sellerMapper;
        this.orderMapper = orderMapper;
        this.returnMapper = returnMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Seller register(SellerRegisterDTO dto) {
        if (sellerMapper.findByUsername(dto.getUsername()) != null) {
            throw new BusinessException("用户名已存在");
        }
        if (sellerMapper.findByPhone(dto.getPhone()) != null) {
            throw new BusinessException("手机号已被注册");
        }
        Seller seller = new Seller();
        seller.setUsername(dto.getUsername());
        seller.setPassword(passwordEncoder.encode(dto.getPassword()));
        seller.setShopName(dto.getShopName());
        seller.setPhone(dto.getPhone());
        seller.setEmail(dto.getEmail());
        sellerMapper.insert(seller);
        return seller;
    }

    @Override
    public LoginVO login(LoginDTO dto) {
        Seller seller = sellerMapper.findByUsername(dto.getUsername());
        if (seller == null) {
            throw new BusinessException("用户名或密码错误");
        }
        if (seller.getStatus() != null && seller.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }
        if (!passwordEncoder.matches(dto.getPassword(), seller.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        sellerMapper.updateLoginTime(seller.getId());

        String token = jwtUtil.generateToken(seller.getId(), seller.getUsername(), "SELLER");
        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setUserId(seller.getId());
        vo.setUsername(seller.getUsername());
        vo.setRole("SELLER");
        vo.setShopName(seller.getShopName());
        return vo;
    }

    @Override
    public Seller getSellerById(Long id) {
        Seller seller = sellerMapper.findById(id);
        if (seller == null) {
            throw new BusinessException("卖家不存在");
        }
        seller.setPassword(null);
        return seller;
    }

    @Override
    public void updateSeller(Seller seller) {
        sellerMapper.update(seller);
    }

    @Override
    public void changePassword(Long sellerId, String oldPassword, String newPassword) {
        Seller seller = sellerMapper.findById(sellerId);
        if (!passwordEncoder.matches(oldPassword, seller.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        sellerMapper.updatePassword(sellerId, passwordEncoder.encode(newPassword));
    }

    @Override
    public List<Return> getReturnList(Long sellerId) {
        return returnMapper.findBySellerId(sellerId);
    }

    @Override
    public Map<String, Object> getSalesStats(Long sellerId) {
        // 统计各状态订单数量
        long pendingPay = orderMapper.countSellerOrders(sellerId, 1);
        long pendingShip = orderMapper.countSellerOrders(sellerId, 2);
        long pendingReceive = orderMapper.countSellerOrders(sellerId, 3);
        long completed = orderMapper.countSellerOrders(sellerId, 4);
        long cancelled = orderMapper.countSellerOrders(sellerId, 5);
        long returning = orderMapper.countSellerOrders(sellerId, 6);
        long returned = orderMapper.countSellerOrders(sellerId, 7);
        long total = pendingPay + pendingShip + pendingReceive + completed + cancelled + returning + returned;

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalOrders", total);
        stats.put("pendingPay", pendingPay);
        stats.put("pendingShip", pendingShip);
        stats.put("pendingReceive", pendingReceive);
        stats.put("completed", completed);
        stats.put("cancelled", cancelled);
        stats.put("returning", returning);
        stats.put("returned", returned);
        return stats;
    }
}
