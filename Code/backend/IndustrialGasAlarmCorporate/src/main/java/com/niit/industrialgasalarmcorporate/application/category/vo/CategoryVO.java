package com.niit.industrialgasalarmcorporate.application.category.vo;

import lombok.Data;

import java.util.List;

@Data
public class CategoryVO {

    private String categoryUuid;
    private String name;
    private String type;
    private String parentUuid;
    private int sortOrder;
    private List<CategoryVO> children;
}
