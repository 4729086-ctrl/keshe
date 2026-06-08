package com.shanxi.shopping.service.impl;

import com.shanxi.shopping.common.BusinessException;
import com.shanxi.shopping.mapper.CartItemMapper;
import com.shanxi.shopping.mapper.ProductMapper;
import com.shanxi.shopping.model.entity.CartItem;
import com.shanxi.shopping.model.entity.Product;
import com.shanxi.shopping.model.vo.CartVO;
import com.shanxi.shopping.service.CartService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    private final CartItemMapper cartItemMapper;
    private final ProductMapper productMapper;

    public CartServiceImpl(CartItemMapper cartItemMapper, ProductMapper productMapper) {
        this.cartItemMapper = cartItemMapper;
        this.productMapper = productMapper;
    }

    @Override
    public void addToCart(Long userId, Long productId, int quantity) {
        Product product = productMapper.findById(productId);
        if (product == null) {
            throw new BusinessException(404, "商品不存在");
        }
        if (product.getStatus() != 1) {
            throw new BusinessException("商品已下架");
        }

        CartItem existing = cartItemMapper.findByUserAndProduct(userId, productId);
        if (existing != null) {
            // 已存在，合并数量
            int newQty = existing.getQuantity() + quantity;
            if (newQty > product.getStock()) {
                throw new BusinessException("库存不足，当前库存: " + product.getStock());
            }
            existing.setQuantity(newQty);
            cartItemMapper.update(existing);
        } else {
            if (quantity > product.getStock()) {
                throw new BusinessException("库存不足，当前库存: " + product.getStock());
            }
            CartItem item = new CartItem();
            item.setUserId(userId);
            item.setProductId(productId);
            item.setQuantity(quantity);
            item.setChecked(1);
            cartItemMapper.insert(item);
        }
    }

    @Override
    public List<CartVO> getCartList(Long userId) {
        return cartItemMapper.findCartByUserId(userId);
    }

    @Override
    public void updateQuantity(Long userId, Long cartItemId, int quantity) {
        CartItem item = cartItemMapper.findById(cartItemId);
        if (item == null || !item.getUserId().equals(userId)) {
            throw new BusinessException("购物车项不存在");
        }
        Product product = productMapper.findById(item.getProductId());
        if (quantity > product.getStock()) {
            throw new BusinessException("库存不足，当前库存: " + product.getStock());
        }
        item.setQuantity(quantity);
        cartItemMapper.update(item);
    }

    @Override
    public void updateChecked(Long userId, Long cartItemId, boolean checked) {
        CartItem item = cartItemMapper.findById(cartItemId);
        if (item == null || !item.getUserId().equals(userId)) {
            throw new BusinessException("购物车项不存在");
        }
        cartItemMapper.updateChecked(cartItemId, checked ? 1 : 0);
    }

    @Override
    public void updateAllChecked(Long userId, boolean checked) {
        List<CartVO> items = cartItemMapper.findCartByUserId(userId);
        for (CartVO item : items) {
            cartItemMapper.updateChecked(item.getId(), checked ? 1 : 0);
        }
    }

    @Override
    public void removeItem(Long userId, Long cartItemId) {
        CartItem item = cartItemMapper.findById(cartItemId);
        if (item == null || !item.getUserId().equals(userId)) {
            throw new BusinessException("购物车项不存在");
        }
        cartItemMapper.deleteById(cartItemId);
    }

    @Override
    public void removeItems(Long userId, List<Long> cartItemIds) {
        cartItemMapper.deleteByIds(cartItemIds);
    }
}
