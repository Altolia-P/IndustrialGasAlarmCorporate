package com.niit.industrialgasalarmcorporate.application.message.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AssignMessageDTO {

    @NotBlank(message = "员工UUID不能为空")
    private String staffUuid;

    @NotBlank(message = "员工姓名不能为空")
    private String staffName;
}
