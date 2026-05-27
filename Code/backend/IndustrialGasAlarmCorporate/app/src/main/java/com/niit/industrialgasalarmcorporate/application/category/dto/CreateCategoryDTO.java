package com.niit.industrialgasalarmcorporate.application.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateCategoryDTO {

    @NotBlank(message = "分类名称不能为空")
    private String name;

    @NotNull(message = "分类类型不能为空")
    private String type;

    private String parentUuid;

    private int sortOrder;
}
