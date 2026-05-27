package com.niit.industrialgasalarmcorporate.application.product.vo;

import lombok.Data;

@Data
public class ProductVO {

    private String productUuid;
    private String name;
    private String description;
    private String coverImage;
    private String categoryUuid;
    private String categoryName;
    private String status;
    private String createdAt;
}
