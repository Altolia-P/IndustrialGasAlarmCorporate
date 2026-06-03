package com.niit.industrialgasalarmcorporate.application.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class CreateProductDTO {

    @NotBlank(message = "产品名称不能为空")
    @Size(max = 100, message = "产品名称不超过100字符")
    private String name;

    @Size(max = 2000, message = "描述不超过2000字符")
    private String description;

    @NotBlank(message = "所属分类不能为空")
    @Size(max = 36, message = "分类UUID不超过36字符")
    private String categoryUuid;

    @Size(max = 500, message = "封面图URL不超过500字符")
    private String coverImage;

    private List<ImageDTO> images;

    private List<AttributeDTO> attributes;

    @Size(max = 20, message = "状态不超过20字符")
    private String status;
}
