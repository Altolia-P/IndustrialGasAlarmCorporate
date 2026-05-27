package com.niit.industrialgasalarmcorporate.application.workorder.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AssignWorkOrderDTO {

    @NotBlank(message = "员工UUID不能为空")
    private String staffUuid;

    @NotBlank(message = "员工姓名不能为空")
    private String staffName;
}
