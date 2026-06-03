package com.niit.industrialgasalarmcorporate.application.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateCategoryDTO {

    @NotBlank(message = "分类名称不能为空")
    @Size(max = 100, message = "分类名称不超过100字符")
    private String name;

    @NotNull(message = "分类类型不能为空")
    @Size(max = 50, message = "分类类型不超过50字符")
    private String type;

    @Size(max = 36, message = "父分类UUID不超过36字符")
    private String parentUuid;

    private int sortOrder;
}
