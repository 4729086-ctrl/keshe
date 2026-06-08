package com.shanxi.shopping.mapper;

import com.shanxi.shopping.model.entity.Category;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 商品分类 Mapper
 */
@Mapper
public interface CategoryMapper {

    @Select("SELECT * FROM categories ORDER BY sort_order ASC")
    List<Category> findAll();

    @Select("SELECT * FROM categories WHERE parent_id = #{parentId} ORDER BY sort_order ASC")
    List<Category> findByParentId(Long parentId);

    @Select("SELECT * FROM categories WHERE id = #{id}")
    Category findById(Long id);

    @Insert("INSERT INTO categories (name, parent_id, sort_order, created_at) VALUES (#{name}, #{parentId}, #{sortOrder}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Category category);
}
