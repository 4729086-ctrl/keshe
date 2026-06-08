package com.shanxi.shopping.mapper;

import com.shanxi.shopping.model.entity.Product;
import com.shanxi.shopping.model.vo.ProductVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 商品 Mapper
 */
@Mapper
public interface ProductMapper {

    @Select("SELECT * FROM products WHERE id = #{id}")
    Product findById(Long id);

    @Select("SELECT * FROM products WHERE seller_id = #{sellerId} ORDER BY created_at DESC")
    List<Product> findBySellerId(Long sellerId);

    /** 分页搜索已上架商品 */
    List<Product> search(@Param("keyword") String keyword,
                         @Param("categoryId") Long categoryId,
                         @Param("offset") int offset,
                         @Param("limit") int limit);

    long countSearch(@Param("keyword") String keyword,
                     @Param("categoryId") Long categoryId);

    /** 查询商品详情（含卖家/分类名称） */
    ProductVO findDetailById(Long id);

    @Insert("INSERT INTO products (seller_id, category_id, name, description, price, stock, main_image, status, created_at) " +
            "VALUES (#{sellerId}, #{categoryId}, #{name}, #{description}, #{price}, #{stock}, #{mainImage}, #{status}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Product product);

    @Update("UPDATE products SET category_id=#{categoryId}, name=#{name}, description=#{description}, " +
            "price=#{price}, stock=#{stock}, main_image=#{mainImage}, status=#{status} WHERE id=#{id}")
    int update(Product product);

    /** 下单时扣减库存（行级锁，防超卖） */
    @Update("UPDATE products SET stock = stock - #{quantity}, sales_count = sales_count + #{quantity} " +
            "WHERE id = #{id} AND stock >= #{quantity}")
    int deductStock(@Param("id") Long id, @Param("quantity") int quantity);

    /** 取消/退货时回补库存 */
    @Update("UPDATE products SET stock = stock + #{quantity} WHERE id = #{id}")
    int addStock(@Param("id") Long id, @Param("quantity") int quantity);
}
