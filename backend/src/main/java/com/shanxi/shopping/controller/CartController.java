package com.shanxi.shopping.controller;

import com.shanxi.shopping.common.Result;
import com.shanxi.shopping.model.dto.AddCartDTO;
import com.shanxi.shopping.model.vo.CartVO;
import com.shanxi.shopping.security.SecurityUtil;
import com.shanxi.shopping.service.CartService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 购物车 Controller
 */
@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    /** 购物车列表 */
    @GetMapping
    public Result<List<CartVO>> list() {
        Long userId = SecurityUtil.getCurrentUserId();
        return Result.ok(cartService.getCartList(userId));
    }

    /** 加入购物车 */
    @PostMapping
    public Result<?> add(@Valid @RequestBody AddCartDTO dto) {
        Long userId = SecurityUtil.getCurrentUserId();
        cartService.addToCart(userId, dto.getProductId(), dto.getQuantity());
        return Result.ok();
    }

    /** 修改数量 */
    @PutMapping("/{id}/quantity")
    public Result<?> updateQuantity(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Long userId = SecurityUtil.getCurrentUserId();
        cartService.updateQuantity(userId, id, body.get("quantity"));
        return Result.ok();
    }

    /** 勾选/取消勾选 */
    @PutMapping("/{id}/checked")
    public Result<?> updateChecked(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        Long userId = SecurityUtil.getCurrentUserId();
        cartService.updateChecked(userId, id, body.get("checked"));
        return Result.ok();
    }

    /** 全选/取消全选 */
    @PutMapping("/check-all")
    public Result<?> checkAll(@RequestBody Map<String, Boolean> body) {
        Long userId = SecurityUtil.getCurrentUserId();
        cartService.updateAllChecked(userId, body.get("checked"));
        return Result.ok();
    }

    /** 删除购物车项 */
    @DeleteMapping("/{id}")
    public Result<?> remove(@PathVariable Long id) {
        Long userId = SecurityUtil.getCurrentUserId();
        cartService.removeItem(userId, id);
        return Result.ok();
    }

    /** 批量删除 */
    @DeleteMapping("/batch")
    public Result<?> removeBatch(@RequestBody Map<String, List<Long>> body) {
        Long userId = SecurityUtil.getCurrentUserId();
        cartService.removeItems(userId, body.get("ids"));
        return Result.ok();
    }
}
