package com.shanxi.shopping.mapper;

import com.shanxi.shopping.model.entity.OrderItem;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 订单明细 Mapper
 */
@Mapper
public interface OrderItemMapper {

    @Select("SELECT * FROM order_items WHERE order_id = #{orderId}")
    List<OrderItem> findByOrderId(Long orderId);

    @Insert("INSERT INTO order_items (order_id, product_id, product_name, product_image, price, quantity, subtotal) " +
            "VALUES (#{orderId}, #{productId}, #{productName}, #{productImage}, #{price}, #{quantity}, #{subtotal})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(OrderItem item);

    /** 批量插入 */
    int insertBatch(@Param("items") List<OrderItem> items);
}
