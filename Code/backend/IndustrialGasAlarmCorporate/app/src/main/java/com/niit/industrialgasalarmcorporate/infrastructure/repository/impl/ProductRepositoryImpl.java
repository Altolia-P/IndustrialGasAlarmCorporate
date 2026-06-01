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

import java.util.*;
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
        List<Product> products = enrichProducts(result.getRecords());
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
        List<Product> products = enrichProducts(result.getRecords());
        return new Page<>(products, result.getTotal(), (int) result.getSize(), (int) result.getCurrent());
    }

    @Override
    public List<Product> searchByKeyword(String keyword, int limit) {
        LambdaQueryWrapper<ProductPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.like(ProductPO::getName, keyword).or().like(ProductPO::getDescription, keyword))
                .eq(ProductPO::getStatus, ProductStatus.PUBLISHED.name())
                .orderByDesc(ProductPO::getCreatedAt)
                .last("LIMIT " + limit);
        List<ProductPO> poList = productMapper.selectList(wrapper);
        return enrichProducts(poList);
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
        List<Product> products = enrichProducts(result.getRecords());
        return new Page<>(products, result.getTotal(), (int) result.getSize(), (int) result.getCurrent());
    }

    private List<Product> enrichProducts(List<ProductPO> records) {
        if (records.isEmpty()) return Collections.emptyList();
        List<String> productUuids = records.stream().map(ProductPO::getProductUuid).distinct().toList();
        Map<String, List<ProductImage>> imagesMap = batchLoadImages(productUuids);
        Map<String, List<ProductAttribute>> attrsMap = batchLoadAttributes(productUuids);
        List<String> categoryUuids = records.stream().map(ProductPO::getCategoryUuid).filter(Objects::nonNull).distinct().toList();
        Map<String, String> categoryNames = categoryUuids.isEmpty() ? Collections.emptyMap() : categoryMapper.selectBatchIds(categoryUuids).stream()
                .collect(Collectors.toMap(c -> c.getCategoryUuid(), c -> c.getName(), (a, b) -> a));
        return records.stream()
                .map(po -> toDomain(po,
                        imagesMap.getOrDefault(po.getProductUuid(), Collections.emptyList()),
                        attrsMap.getOrDefault(po.getProductUuid(), Collections.emptyList()),
                        categoryNames.get(po.getCategoryUuid())))
                .collect(Collectors.toList());
    }

    private Map<String, List<ProductImage>> batchLoadImages(List<String> productUuids) {
        LambdaQueryWrapper<ProductImagePO> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(ProductImagePO::getProductUuid, productUuids)
                .orderByAsc(ProductImagePO::getSortOrder);
        return productImageMapper.selectList(wrapper).stream()
                .collect(Collectors.groupingBy(
                        ProductImagePO::getProductUuid,
                        Collectors.mapping(this::toImageDomain, Collectors.toList())
                ));
    }

    private Map<String, List<ProductAttribute>> batchLoadAttributes(List<String> productUuids) {
        LambdaQueryWrapper<ProductAttributePO> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(ProductAttributePO::getProductUuid, productUuids);
        return productAttributeMapper.selectList(wrapper).stream()
                .collect(Collectors.groupingBy(
                        ProductAttributePO::getProductUuid,
                        Collectors.mapping(this::toAttributeDomain, Collectors.toList())
                ));
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
        return toDomain(po, images, attributes, null);
    }

    private Product toDomain(ProductPO po, List<ProductImage> images, List<ProductAttribute> attributes, String categoryName) {
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
