package com.niit.industrialgasalarmcorporate.application.alert.vo;

import lombok.Data;

@Data
public class AlertVO {

    private String alertUuid;
    private String deviceUuid;
    private String ruleUuid;
    private String alertType;
    private String severity;
    private String concentration;
    private String threshold;
    private String message;
    private String status;
    private String triggeredAt;
    private String confirmedAt;
    private String confirmedBy;
    private String resolvedAt;
    private String resolvedBy;
    private String workOrderUuid;
    private String createdAt;
}
