package com.niit.industrialgasalarmcorporate.application.workorder.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CompleteWorkOrderDTO {

    @NotBlank(message = "处理结果不能为空")
    private String resolution;
}
