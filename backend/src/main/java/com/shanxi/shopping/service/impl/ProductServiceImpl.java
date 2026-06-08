package com.shanxi.shopping.service.impl;

import com.shanxi.shopping.common.BusinessException;
import com.shanxi.shopping.mapper.ProductImageMapper;
import com.shanxi.shopping.mapper.ProductMapper;
import com.shanxi.shopping.model.entity.Product;
import com.shanxi.shopping.model.entity.ProductImage;
import com.shanxi.shopping.model.vo.PageVO;
import com.shanxi.shopping.model.vo.ProductVO;
import com.shanxi.shopping.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;
    private final ProductImageMapper imageMapper;

    public ProductServiceImpl(ProductMapper productMapper, ProductImageMapper imageMapper) {
        this.productMapper = productMapper;
        this.imageMapper = imageMapper;
    }

    @Override
    public PageVO<Product> search(String keyword, Long categoryId, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<Product> list = productMapper.search(keyword, categoryId, offset, pageSize);
        long total = productMapper.countSearch(keyword, categoryId);
        return new PageVO<>(list, total, page, pageSize);
    }

    @Override
    public ProductVO getDetail(Long id) {
        ProductVO vo = productMapper.findDetailById(id);
        if (vo == null) {
            throw new BusinessException(404, "商品不存在");
        }
        // 查询商品多图
        List<ProductImage> images = imageMapper.findByProductId(id);
        vo.setImages(images.stream().map(ProductImage::getImageUrl).collect(Collectors.toList()));
        return vo;
    }

    @Override
    public PageVO<Product> getBySeller(Long sellerId, int page, int pageSize) {
        List<Product> all = productMapper.findBySellerId(sellerId);
        int offset = (page - 1) * pageSize;
        int end = Math.min(offset + pageSize, all.size());
        List<Product> list = all.subList(Math.min(offset, all.size()), end);
        return new PageVO<>(list, all.size(), page, pageSize);
    }

    @Override
    @Transactional
    public Product addProduct(Long sellerId, Product product, List<String> imageUrls) {
        product.setSellerId(sellerId);
        product.setSalesCount(0);
        productMapper.insert(product);

        // 保存图片
        if (imageUrls != null) {
            for (int i = 0; i < imageUrls.size(); i++) {
                ProductImage img = new ProductImage();
                img.setProductId(product.getId());
                img.setImageUrl(imageUrls.get(i));
                img.setSortOrder(i);
                imageMapper.insert(img);
            }
        }
        return product;
    }

    @Override
    @Transactional
    public void updateProduct(Long sellerId, Product product, List<String> imageUrls) {
        Product existing = productMapper.findById(product.getId());
        if (existing == null || !existing.getSellerId().equals(sellerId)) {
            throw new BusinessException("无权编辑此商品");
        }
        productMapper.update(product);

        // 更新图片：先删后插
        imageMapper.deleteByProductId(product.getId());
        if (imageUrls != null) {
            for (int i = 0; i < imageUrls.size(); i++) {
                ProductImage img = new ProductImage();
                img.setProductId(product.getId());
                img.setImageUrl(imageUrls.get(i));
                img.setSortOrder(i);
                imageMapper.insert(img);
            }
        }
    }

    @Override
    public void updateStatus(Long sellerId, Long productId, int status) {
        Product existing = productMapper.findById(productId);
        if (existing == null || !existing.getSellerId().equals(sellerId)) {
            throw new BusinessException("无权操作此商品");
        }
        existing.setStatus(status);
        productMapper.update(existing);
    }
}
