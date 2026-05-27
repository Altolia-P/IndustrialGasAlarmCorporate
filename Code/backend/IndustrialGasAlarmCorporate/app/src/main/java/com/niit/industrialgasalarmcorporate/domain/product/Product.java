package com.niit.industrialgasalarmcorporate.domain.product;

import com.niit.industrialgasalarmcorporate.common.enums.ErrorCode;
import com.niit.industrialgasalarmcorporate.common.exception.BusinessException;
import com.niit.industrialgasalarmcorporate.common.exception.ProductCannotPublishException;
import com.niit.industrialgasalarmcorporate.domain.shared.AggregateRoot;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Product extends AggregateRoot {

    private final String productUuid;
    private String name;
    private String description;
    private ProductStatus status;
    private String coverImage;
    private List<ProductImage> images;
    private List<ProductAttribute> attributes;
    private String categoryUuid;
    private String categoryName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Product(String name, String description, String coverImage,
                   String categoryUuid, ProductStatus status) {
        this.productUuid = UUID.randomUUID().toString();
        this.name = name;
        this.description = description;
        this.coverImage = coverImage;
        this.categoryUuid = categoryUuid;
        this.status = status;
        this.images = new ArrayList<>();
        this.attributes = new ArrayList<>();
    }

    public Product(String productUuid, String name, String description, ProductStatus status,
                   String coverImage, List<ProductImage> images,
                   List<ProductAttribute> attributes, String categoryUuid,
                   String categoryName, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.productUuid = productUuid;
        this.name = name;
        this.description = description;
        this.status = status;
        this.coverImage = coverImage;
        this.images = new ArrayList<>(images != null ? images : Collections.emptyList());
        this.attributes = new ArrayList<>(attributes != null ? attributes : Collections.emptyList());
        this.categoryUuid = categoryUuid;
        this.categoryName = categoryName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void publish() {
        if (this.status == ProductStatus.PUBLISHED) {
            throw new ProductCannotPublishException("产品已是上架状态");
        }
        this.status = ProductStatus.PUBLISHED;
    }

    public void unpublish() {
        if (this.status != ProductStatus.PUBLISHED) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "当前产品非上架状态，无法下架");
        }
        this.status = ProductStatus.UNPUBLISHED;
    }

    public void addImage(ProductImage image) {
        this.images.add(image);
    }

    public void addAttribute(ProductAttribute attribute) {
        this.attributes.add(attribute);
    }

    public void update(String name, String description, String coverImage, String categoryUuid) {
        this.name = name;
        this.description = description;
        this.coverImage = coverImage;
        this.categoryUuid = categoryUuid;
    }

    public String getProductUuid() {
        return productUuid;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public List<ProductImage> getImages() {
        return Collections.unmodifiableList(images);
    }

    public List<ProductAttribute> getAttributes() {
        return Collections.unmodifiableList(attributes);
    }

    public String getCategoryUuid() {
        return categoryUuid;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setImages(List<ProductImage> images) {
        this.images = new ArrayList<>(images);
    }

    public void setAttributes(List<ProductAttribute> attributes) {
        this.attributes = new ArrayList<>(attributes);
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }
}
