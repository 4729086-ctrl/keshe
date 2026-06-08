package com.shanxi.shopping.service;

import com.shanxi.shopping.model.vo.CartVO;

import java.util.List;

/**
 * 购物车服务接口
 */
public interface CartService {

    /** 加入购物车（重复则合并数量） */
    void addToCart(Long userId, Long productId, int quantity);

    /** 查看购物车列表 */
    List<CartVO> getCartList(Long userId);

    /** 修改数量 */
    void updateQuantity(Long userId, Long cartItemId, int quantity);

    /** 勾选/取消勾选 */
    void updateChecked(Long userId, Long cartItemId, boolean checked);

    /** 全选/取消全选 */
    void updateAllChecked(Long userId, boolean checked);

    /** 删除购物车项 */
    void removeItem(Long userId, Long cartItemId);

    /** 批量删除 */
    void removeItems(Long userId, List<Long> cartItemIds);
}
