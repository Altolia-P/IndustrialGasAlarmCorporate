package com.niit.industrialgasalarmcorporate.application.workorder.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateWorkOrderDTO {

    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "类型不能为空")
    private String type;

    private String description;

    @NotBlank(message = "优先级不能为空")
    private String priority;

    @NotBlank(message = "客户名称不能为空")
    private String customerName;

    private String customerPhone;

    private String assignedStaffUuid;

    private String assignedStaffName;
}
