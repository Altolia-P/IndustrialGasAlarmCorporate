package com.niit.industrialgasalarmcorporate.application.workorder.dto;

import lombok.Data;

@Data
public class UpdateWorkOrderDTO {

    private String title;

    private String type;

    private String description;

    private String priority;

    private String customerName;

    private String customerPhone;

    private String assignedStaffUuid;

    private String assignedStaffName;

    private String resolution;
}
