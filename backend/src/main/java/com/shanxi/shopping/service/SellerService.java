package com.shanxi.shopping.service;

import com.shanxi.shopping.model.dto.LoginDTO;
import com.shanxi.shopping.model.dto.SellerRegisterDTO;
import com.shanxi.shopping.model.entity.Return;
import com.shanxi.shopping.model.entity.Seller;
import com.shanxi.shopping.model.vo.LoginVO;

import java.util.List;
import java.util.Map;

/**
 * 卖家服务接口
 */
public interface SellerService {

    /** 卖家注册 */
    Seller register(SellerRegisterDTO dto);

    /** 卖家登录 */
    LoginVO login(LoginDTO dto);

    /** 获取卖家信息 */
    Seller getSellerById(Long id);

    /** 更新店铺信息 */
    void updateSeller(Seller seller);

    /** 修改密码 */
    void changePassword(Long sellerId, String oldPassword, String newPassword);

    /** 退货单列表（卖家视角） */
    List<Return> getReturnList(Long sellerId);

    /** 销售统计 */
    Map<String, Object> getSalesStats(Long sellerId);
}
