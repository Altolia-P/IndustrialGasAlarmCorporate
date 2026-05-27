package com.niit.industrialgasalarmcorporate.application.message.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProcessMessageDTO {

    @NotBlank(message = "处理备注不能为空")
    private String remark;
}
