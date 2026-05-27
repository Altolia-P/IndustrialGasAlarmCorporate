package com.niit.industrialgasalarmcorporate.application.product.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class UpdateProductDTO {

    @Size(max = 100, message = "产品名称不超过100字符")
    private String name;

    private String description;

    private String categoryUuid;

    private String coverImage;

    private List<ImageDTO> images;

    private List<AttributeDTO> attributes;

    private String status;
}
