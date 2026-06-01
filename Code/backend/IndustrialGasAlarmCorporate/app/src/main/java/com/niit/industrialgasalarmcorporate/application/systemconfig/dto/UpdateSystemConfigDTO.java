package com.niit.industrialgasalarmcorporate.application.systemconfig.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateSystemConfigDTO {

    @NotBlank(message = "配置值不能为空")
    private String configValue;

    private String description;
}
