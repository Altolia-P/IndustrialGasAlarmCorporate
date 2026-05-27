package com.niit.industrialgasalarmcorporate.application.product.vo;

import lombok.Data;

import java.util.List;

@Data
public class ProductDetailVO {

    private String productUuid;
    private String name;
    private String description;
    private String coverImage;
    private List<ImageVO> images;
    private List<AttributeVO> attributes;
    private String categoryUuid;
    private String categoryName;
    private String status;
    private String createdAt;
}
