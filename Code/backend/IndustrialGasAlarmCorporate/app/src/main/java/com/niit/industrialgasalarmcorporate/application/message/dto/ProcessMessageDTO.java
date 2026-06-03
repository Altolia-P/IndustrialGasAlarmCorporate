package com.niit.industrialgasalarmcorporate.application.message.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProcessMessageDTO {

    @NotBlank(message = "处理备注不能为空")
    @Size(max = 500, message = "备注不超过500字符")
    private String remark;
}
