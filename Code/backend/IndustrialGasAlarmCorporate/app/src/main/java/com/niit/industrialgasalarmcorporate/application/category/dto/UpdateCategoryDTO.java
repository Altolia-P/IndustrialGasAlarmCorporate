package com.niit.industrialgasalarmcorporate.application.category.dto;

import lombok.Data;

@Data
public class UpdateCategoryDTO {

    private String name;

    private String type;

    private String parentUuid;

    private Integer sortOrder;
}
