package com.shanxi.shopping.controller;

import com.shanxi.shopping.common.Result;
import com.shanxi.shopping.model.dto.LoginDTO;
import com.shanxi.shopping.model.dto.SellerRegisterDTO;
import com.shanxi.shopping.model.entity.Product;
import com.shanxi.shopping.model.entity.Return;
import com.shanxi.shopping.model.entity.Seller;
import com.shanxi.shopping.model.vo.OrderVO;
import com.shanxi.shopping.model.vo.PageVO;
import com.shanxi.shopping.security.SecurityUtil;
import com.shanxi.shopping.service.OrderService;
import com.shanxi.shopping.service.ProductService;
import com.shanxi.shopping.service.SellerService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 卖家 Controller
 */
@RestController
@RequestMapping("/api/seller")
public class SellerController {

    private final SellerService sellerService;
    private final ProductService productService;
    private final OrderService orderService;

    public SellerController(SellerService sellerService, ProductService productService, OrderService orderService) {
        this.sellerService = sellerService;
        this.productService = productService;
        this.orderService = orderService;
    }

    /** 注册 */
    @PostMapping("/register")
    public Result<?> register(@Valid @RequestBody SellerRegisterDTO dto) {
        sellerService.register(dto);
        return Result.ok();
    }

    /** 登录 */
    @PostMapping("/login")
    public Result<?> login(@Valid @RequestBody LoginDTO dto) {
        return Result.ok(sellerService.login(dto));
    }

    /** 店铺信息 */
    @GetMapping("/profile")
    public Result<Seller> profile() {
        Long sellerId = SecurityUtil.getCurrentUserId();
        return Result.ok(sellerService.getSellerById(sellerId));
    }

    /** 修改店铺信息 */
    @PutMapping("/profile")
    public Result<?> updateProfile(@RequestBody Seller seller) {
        Long sellerId = SecurityUtil.getCurrentUserId();
        seller.setId(sellerId);
        sellerService.updateSeller(seller);
        return Result.ok();
    }

    /** 修改密码 */
    @PutMapping("/password")
    public Result<?> changePassword(@RequestBody Map<String, String> params) {
        Long sellerId = SecurityUtil.getCurrentUserId();
        sellerService.changePassword(sellerId, params.get("oldPassword"), params.get("newPassword"));
        return Result.ok();
    }

    // ---- 商品管理 ----

    /** 我的商品列表 */
    @GetMapping("/products")
    public Result<PageVO<Product>> products(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        Long sellerId = SecurityUtil.getCurrentUserId();
        return Result.ok(productService.getBySeller(sellerId, page, pageSize));
    }

    /** 新增商品 */
    @PostMapping("/products")
    public Result<Product> addProduct(@RequestBody Map<String, Object> body) {
        Long sellerId = SecurityUtil.getCurrentUserId();
        Product product = parseProduct(body);
        @SuppressWarnings("unchecked")
        List<String> images = (List<String>) body.get("images");
        return Result.ok(productService.addProduct(sellerId, product, images));
    }

    /** 编辑商品 */
    @PutMapping("/products/{id}")
    public Result<?> updateProduct(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Long sellerId = SecurityUtil.getCurrentUserId();
        Product product = parseProduct(body);
        product.setId(id);
        @SuppressWarnings("unchecked")
        List<String> images = (List<String>) body.get("images");
        productService.updateProduct(sellerId, product, images);
        return Result.ok();
    }

    /** 上架/下架 */
    @PutMapping("/products/{id}/status")
    public Result<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Long sellerId = SecurityUtil.getCurrentUserId();
        productService.updateStatus(sellerId, id, body.get("status"));
        return Result.ok();
    }

    // ---- 订单处理 ----

    /** 订单列表 */
    @GetMapping("/orders")
    public Result<PageVO<OrderVO>> orders(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        Long sellerId = SecurityUtil.getCurrentUserId();
        return Result.ok(orderService.getSellerOrders(sellerId, status, page, pageSize));
    }

    /** 订单详情 */
    @GetMapping("/orders/{id}")
    public Result<OrderVO> orderDetail(@PathVariable Long id) {
        return Result.ok(orderService.getOrderDetail(id));
    }

    /** 发货 */
    @PutMapping("/orders/{id}/ship")
    public Result<?> ship(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Long sellerId = SecurityUtil.getCurrentUserId();
        orderService.shipOrder(sellerId, id, body.get("logisticsCompany"), body.get("logisticsNo"));
        return Result.ok();
    }

    // ---- 退货处理 ----

    /** 退货列表 */
    @GetMapping("/returns")
    public Result<List<Return>> returns() {
        Long sellerId = SecurityUtil.getCurrentUserId();
        return Result.ok(sellerService.getReturnList(sellerId));
    }

    /** 处理退货 */
    @PutMapping("/returns/{id}")
    public Result<?> processReturn(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Long sellerId = SecurityUtil.getCurrentUserId();
        boolean approved = (boolean) body.get("approved");
        String remark = (String) body.getOrDefault("remark", "");
        orderService.processReturn(sellerId, id, approved, remark);
        return Result.ok();
    }

    // ---- 销售统计 ----

    /** 销售统计 */
    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        Long sellerId = SecurityUtil.getCurrentUserId();
        return Result.ok(sellerService.getSalesStats(sellerId));
    }

    // 辅助：从 Map 解析 Product
    private Product parseProduct(Map<String, Object> body) {
        Product p = new Product();
        if (body.get("categoryId") != null) {
            p.setCategoryId(Long.valueOf(body.get("categoryId").toString()));
        }
        p.setName((String) body.get("name"));
        p.setDescription((String) body.get("description"));
        if (body.get("price") != null) {
            p.setPrice(new java.math.BigDecimal(body.get("price").toString()));
        }
        if (body.get("stock") != null) {
            p.setStock((Integer) body.get("stock"));
        }
        p.setMainImage((String) body.get("mainImage"));
        if (body.get("status") != null) {
            p.setStatus((Integer) body.get("status"));
        }
        return p;
    }
}
