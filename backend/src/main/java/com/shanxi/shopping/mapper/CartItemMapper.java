package com.shanxi.shopping.mapper;

import com.shanxi.shopping.model.entity.CartItem;
import com.shanxi.shopping.model.vo.CartVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 购物车 Mapper
 */
@Mapper
public interface CartItemMapper {

    /** 查询用户购物车（含商品信息） */
    List<CartVO> findCartByUserId(Long userId);

    @Select("SELECT * FROM cart_items WHERE id = #{id}")
    CartItem findById(Long id);

    @Select("SELECT * FROM cart_items WHERE user_id = #{userId} AND product_id = #{productId}")
    CartItem findByUserAndProduct(@Param("userId") Long userId, @Param("productId") Long productId);

    @Select("SELECT * FROM cart_items WHERE user_id = #{userId} AND checked = 1")
    List<CartItem> findCheckedByUserId(Long userId);

    @Insert("INSERT INTO cart_items (user_id, product_id, quantity, checked, created_at) " +
            "VALUES (#{userId}, #{productId}, #{quantity}, #{checked}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(CartItem item);

    @Update("UPDATE cart_items SET quantity = #{quantity}, checked = #{checked} WHERE id = #{id}")
    int update(CartItem item);

    @Update("UPDATE cart_items SET quantity = quantity + #{delta} WHERE id = #{id}")
    int updateQuantity(@Param("id") Long id, @Param("delta") int delta);

    @Update("UPDATE cart_items SET checked = #{checked} WHERE id = #{id}")
    int updateChecked(@Param("id") Long id, @Param("checked") int checked);

    @Delete("DELETE FROM cart_items WHERE id = #{id}")
    int deleteById(Long id);

    /** 批量删除（下单后清空已购买的商品） */
    @Delete("<script>DELETE FROM cart_items WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>#{id}</foreach></script>")
    int deleteByIds(@Param("ids") List<Long> ids);
}
