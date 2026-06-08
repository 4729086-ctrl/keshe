package com.shanxi.shopping.service;

import com.shanxi.shopping.model.entity.Product;
import com.shanxi.shopping.model.vo.PageVO;
import com.shanxi.shopping.model.vo.ProductVO;

import java.util.List;

/**
 * 商品服务接口
 */
public interface ProductService {

    /** 分页搜索商品（上架状态） */
    PageVO<Product> search(String keyword, Long categoryId, int page, int pageSize);

    /** 商品详情（含多图、卖家信息） */
    ProductVO getDetail(Long id);

    /** 按卖家分页查询商品 */
    PageVO<Product> getBySeller(Long sellerId, int page, int pageSize);

    /** 卖家新增商品 */
    Product addProduct(Long sellerId, Product product, List<String> imageUrls);

    /** 卖家编辑商品 */
    void updateProduct(Long sellerId, Product product, List<String> imageUrls);

    /** 上架/下架 */
    void updateStatus(Long sellerId, Long productId, int status);
}
