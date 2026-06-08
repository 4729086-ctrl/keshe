package com.shanxi.shopping.mapper;

import com.shanxi.shopping.model.entity.Return;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 退货记录 Mapper
 */
@Mapper
public interface ReturnMapper {

    @Select("SELECT * FROM `returns` WHERE id = #{id}")
    Return findById(Long id);

    @Select("SELECT * FROM `returns` WHERE order_id = #{orderId}")
    Return findByOrderId(Long orderId);

    @Select("SELECT * FROM `returns` WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<Return> findByUserId(Long userId);

    @Select("SELECT r.* FROM `returns` r INNER JOIN orders o ON r.order_id = o.id " +
            "WHERE o.seller_id = #{sellerId} ORDER BY r.created_at DESC")
    List<Return> findBySellerId(Long sellerId);

    @Insert("INSERT INTO `returns` (order_id, user_id, reason, status, refund_amount, created_at) " +
            "VALUES (#{orderId}, #{userId}, #{reason}, #{status}, #{refundAmount}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Return r);

    @Update("UPDATE `returns` SET status=#{status}, seller_remark=#{sellerRemark}, processed_at=NOW() WHERE id=#{id}")
    int process(@Param("id") Long id, @Param("status") int status, @Param("sellerRemark") String sellerRemark);
}
