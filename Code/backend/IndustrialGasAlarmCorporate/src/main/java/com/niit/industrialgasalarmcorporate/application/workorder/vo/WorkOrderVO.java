package com.niit.industrialgasalarmcorporate.application.workorder.vo;

import lombok.Data;

@Data
public class WorkOrderVO {

    private String workOrderUuid;
    private String title;
    private String type;
    private String description;
    private String status;
    private String priority;
    private String customerName;
    private String customerPhone;
    private String assignedStaffUuid;
    private String assignedStaffName;
    private String resolution;
    private String createdAt;
    private String updatedAt;
}
