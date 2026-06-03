package com.niit.industrialgasalarmcorporate.application.workorder.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateWorkOrderDTO {

    @Size(max = 100, message = "标题不超过100字符")
    private String title;

    @Size(max = 50, message = "类型不超过50字符")
    private String type;

    @Size(max = 500, message = "描述不超过500字符")
    private String description;

    @Size(max = 20, message = "优先级不超过20字符")
    private String priority;

    @Size(max = 100, message = "客户名称不超过100字符")
    private String customerName;

    @Size(max = 20, message = "客户电话不超过20字符")
    private String customerPhone;

    @Size(max = 36, message = "指派员工UUID不超过36字符")
    private String assignedStaffUuid;

    @Size(max = 100, message = "指派员工姓名不超过100字符")
    private String assignedStaffName;

    @Size(max = 500, message = "处理方案不超过500字符")
    private String resolution;
}
