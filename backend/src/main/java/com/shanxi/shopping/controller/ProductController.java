package com.shanxi.shopping.controller;

import com.shanxi.shopping.common.Result;
import com.shanxi.shopping.mapper.CategoryMapper;
import com.shanxi.shopping.model.entity.Category;
import com.shanxi.shopping.model.vo.PageVO;
import com.shanxi.shopping.model.vo.ProductVO;
import com.shanxi.shopping.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品 Controller（公开访问）
 */
@RestController
@RequestMapping("/api")
public class ProductController {

    private final ProductService productService;
    private final CategoryMapper categoryMapper;

    public ProductController(ProductService productService, CategoryMapper categoryMapper) {
        this.productService = productService;
        this.categoryMapper = categoryMapper;
    }

    /** 商品搜索（分页） */
    @GetMapping("/products")
    public Result<PageVO<?>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return Result.ok(productService.search(keyword, categoryId, page, pageSize));
    }

    /** 商品详情 */
    @GetMapping("/products/{id:\\d+}")
    public Result<ProductVO> detail(@PathVariable Long id) {
        return Result.ok(productService.getDetail(id));
    }

    /** 全部分类 */
    @GetMapping("/categories")
    public Result<List<Category>> categories() {
        return Result.ok(categoryMapper.findAll());
    }

    /** 子分类 */
    @GetMapping("/categories/{parentId}/children")
    public Result<List<Category>> children(@PathVariable Long parentId) {
        return Result.ok(categoryMapper.findByParentId(parentId));
    }
}
