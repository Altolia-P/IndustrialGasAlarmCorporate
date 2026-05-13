package com.niit.industrialgasalarmcorporate.infrastructure.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.niit.industrialgasalarmcorporate.common.base.Page;
import com.niit.industrialgasalarmcorporate.domain.product.Product;
import com.niit.industrialgasalarmcorporate.domain.product.ProductAttribute;
import com.niit.industrialgasalarmcorporate.domain.product.ProductImage;
import com.niit.industrialgasalarmcorporate.domain.product.ProductRepository;
import com.niit.industrialgasalarmcorporate.domain.product.ProductStatus;
import com.niit.industrialgasalarmcorporate.infrastructure.repository.mapper.CategoryMapper;
import com.niit.industrialgasalarmcorporate.infrastructure.repository.mapper.ProductAttributeMapper;
import com.niit.industrialgasalarmcorporate.infrastructure.repository.mapper.ProductImageMapper;
import com.niit.industrialgasalarmcorporate.infrastructure.repository.mapper.ProductMapper;
import com.niit.industrialgasalarmcorporate.infrastructure.repository.po.ProductAttributePO;
import com.niit.industrialgasalarmcorporate.infrastructure.repository.po.ProductImagePO;
import com.niit.industrialgasalarmcorporate.infrastructure.repository.po.ProductPO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductMapper productMapper;
    private final ProductImageMapper productImageMapper;
    private final ProductAttributeMapper productAttributeMapper;
    private final CategoryMapper categoryMapper;

    @Override
    public Optional<Product> findById(String productUuid) {
        ProductPO po = productMapper.selectById(productUuid);
        if (po == null) {
            return Optional.empty();
        }
        List<ProductImage> images = findImagesByProductUuid(productUuid);
        List<ProductAttribute> attributes = findAttributesByProductUuid(productUuid);
        return Optional.of(toDomain(po, images, attributes));
    }

    @Override
    public void save(Product product) {
        ProductPO po = toPO(product);
        ProductPO existing = productMapper.selectById(product.getProductUuid());
        if (existing != null) {
            productMapper.updateById(po);
        } else {
            productMapper.insert(po);
        }
        saveImages(product.getProductUuid(), product.getImages());
        saveAttributes(product.getProductUuid(), product.getAttributes());
    }

    @Override
    public void deleteById(String productUuid) {
        productMapper.deleteById(productUuid);
    }

    @Override
    public Page<Product> findByCategory(String categoryUuid, int page, int size) {
        LambdaQueryWrapper<ProductPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductPO::getCategoryUuid, categoryUuid)
                .eq(ProductPO::getStatus, ProductStatus.PUBLISHED.name())
                .orderByDesc(ProductPO::getCreatedAt);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ProductPO> mpPage =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ProductPO> result =
                productMapper.selectPage(mpPage, wrapper);
        List<Product> products = result.getRecords().stream()
                .map(po -> toDomain(po, findImagesByProductUuid(po.getProductUuid()),
                        findAttributesByProductUuid(po.getProductUuid())))
                .collect(Collectors.toList());
        return new Page<>(products, result.getTotal(), (int) result.getSize(), (int) result.getCurrent());
    }

    @Override
    public Page<Product> findAll(int page, int size) {
        LambdaQueryWrapper<ProductPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductPO::getStatus, ProductStatus.PUBLISHED.name())
                .orderByDesc(ProductPO::getCreatedAt);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ProductPO> mpPage =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ProductPO> result =
                productMapper.selectPage(mpPage, wrapper);
        List<Product> products = result.getRecords().stream()
                .map(po -> toDomain(po, findImagesByProductUuid(po.getProductUuid()),
                        findAttributesByProductUuid(po.getProductUuid())))
                .collect(Collectors.toList());
        return new Page<>(products, result.getTotal(), (int) result.getSize(), (int) result.getCurrent());
    }

    @Override
    public Page<Product> findAllWithFilter(String name, String categoryUuid, String status, int page, int size) {
        LambdaQueryWrapper<ProductPO> wrapper = new LambdaQueryWrapper<>();
        if (name != null && !name.isBlank()) {
            wrapper.like(ProductPO::getName, name);
        }
        if (categoryUuid != null && !categoryUuid.isBlank()) {
            wrapper.eq(ProductPO::getCategoryUuid, categoryUuid);
        }
        if (status != null && !status.isBlank()) {
            wrapper.eq(ProductPO::getStatus, status);
        }
        wrapper.orderByDesc(ProductPO::getCreatedAt);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ProductPO> mpPage =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ProductPO> result =
                productMapper.selectPage(mpPage, wrapper);
        List<Product> products = result.getRecords().stream()
                .map(po -> toDomain(po, findImagesByProductUuid(po.getProductUuid()),
                        findAttributesByProductUuid(po.getProductUuid())))
                .collect(Collectors.toList());
        return new Page<>(products, result.getTotal(), (int) result.getSize(), (int) result.getCurrent());
    }

    private List<ProductImage> findImagesByProductUuid(String productUuid) {
        LambdaQueryWrapper<ProductImagePO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductImagePO::getProductUuid, productUuid)
                .orderByAsc(ProductImagePO::getSortOrder);
        return productImageMapper.selectList(wrapper).stream()
                .map(this::toImageDomain)
                .collect(Collectors.toList());
    }

    private List<ProductAttribute> findAttributesByProductUuid(String productUuid) {
        LambdaQueryWrapper<ProductAttributePO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductAttributePO::getProductUuid, productUuid);
        return productAttributeMapper.selectList(wrapper).stream()
                .map(this::toAttributeDomain)
                .collect(Collectors.toList());
    }

    private void saveImages(String productUuid, List<ProductImage> images) {
        LambdaQueryWrapper<ProductImagePO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductImagePO::getProductUuid, productUuid);
        productImageMapper.delete(wrapper);
        for (ProductImage image : images) {
            ProductImagePO po = toImagePO(productUuid, image);
            productImageMapper.insert(po);
        }
    }

    private void saveAttributes(String productUuid, List<ProductAttribute> attributes) {
        LambdaQueryWrapper<ProductAttributePO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductAttributePO::getProductUuid, productUuid);
        productAttributeMapper.delete(wrapper);
        for (ProductAttribute attribute : attributes) {
            ProductAttributePO po = toAttributePO(productUuid, attribute);
            productAttributeMapper.insert(po);
        }
    }

    private Product toDomain(ProductPO po, List<ProductImage> images, List<ProductAttribute> attributes) {
        String categoryName = null;
        if (po.getCategoryUuid() != null) {
            var cat = categoryMapper.selectById(po.getCategoryUuid());
            if (cat != null) {
                categoryName = cat.getName();
            }
        }
        return new Product(
                po.getProductUuid(),
                po.getName(),
                po.getDescription(),
                ProductStatus.valueOf(po.getStatus()),
                po.getCoverImage(),
                images,
                attributes,
                po.getCategoryUuid(),
                categoryName,
                po.getCreatedAt(),
                po.getUpdatedAt()
        );
    }

    private ProductPO toPO(Product product) {
        ProductPO po = new ProductPO();
        po.setProductUuid(product.getProductUuid());
        po.setName(product.getName());
        po.setDescription(product.getDescription());
        po.setStatus(product.getStatus().name());
        po.setCoverImage(product.getCoverImage());
        po.setCategoryUuid(product.getCategoryUuid());
        return po;
    }

    private ProductImage toImageDomain(ProductImagePO po) {
        return new ProductImage(po.getUrl(), po.getAltText(), po.getSortOrder());
    }

    private ProductImagePO toImagePO(String productUuid, ProductImage image) {
        ProductImagePO po = new ProductImagePO();
        po.setProductUuid(productUuid);
        po.setUrl(image.getUrl());
        po.setAltText(image.getAltText());
        po.setSortOrder(image.getSortOrder());
        return po;
    }

    private ProductAttribute toAttributeDomain(ProductAttributePO po) {
        return new ProductAttribute(po.getAttrKey(), po.getAttrVal());
    }

    private ProductAttributePO toAttributePO(String productUuid, ProductAttribute attribute) {
        ProductAttributePO po = new ProductAttributePO();
        po.setProductUuid(productUuid);
        po.setAttrKey(attribute.getAttrKey());
        po.setAttrVal(attribute.getAttrVal());
        return po;
    }
}
