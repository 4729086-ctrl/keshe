package com.shanxi.shopping.mapper;

import com.shanxi.shopping.model.entity.ProductImage;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 商品图片 Mapper
 */
@Mapper
public interface ProductImageMapper {

    @Select("SELECT * FROM product_images WHERE product_id = #{productId} ORDER BY sort_order ASC")
    List<ProductImage> findByProductId(Long productId);

    @Insert("INSERT INTO product_images (product_id, image_url, sort_order, created_at) " +
            "VALUES (#{productId}, #{imageUrl}, #{sortOrder}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ProductImage image);

    @Delete("DELETE FROM product_images WHERE product_id = #{productId}")
    int deleteByProductId(Long productId);
}
