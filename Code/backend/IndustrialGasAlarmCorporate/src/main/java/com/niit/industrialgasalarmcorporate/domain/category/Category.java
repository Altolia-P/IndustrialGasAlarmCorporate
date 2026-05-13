package com.niit.industrialgasalarmcorporate.domain.category;

import java.util.UUID;

public class Category {

    private final String categoryUuid;
    private String name;
    private CategoryType type;
    private String parentUuid;
    private int sortOrder;

    public Category(String name, CategoryType type, String parentUuid, int sortOrder) {
        this.categoryUuid = UUID.randomUUID().toString();
        this.name = name;
        this.type = type;
        this.parentUuid = parentUuid;
        this.sortOrder = sortOrder;
    }

    public Category(String categoryUuid, String name, CategoryType type, String parentUuid, int sortOrder) {
        this.categoryUuid = categoryUuid;
        this.name = name;
        this.type = type;
        this.parentUuid = parentUuid;
        this.sortOrder = sortOrder;
    }

    public String getCategoryUuid() {
        return categoryUuid;
    }

    public String getName() {
        return name;
    }

    public CategoryType getType() {
        return type;
    }

    public String getParentUuid() {
        return parentUuid;
    }

    public int getSortOrder() {
        return sortOrder;
    }
}
