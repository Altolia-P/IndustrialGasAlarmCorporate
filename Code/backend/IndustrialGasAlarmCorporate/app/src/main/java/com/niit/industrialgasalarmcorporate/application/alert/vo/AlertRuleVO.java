package com.niit.industrialgasalarmcorporate.application.alert.vo;

import lombok.Data;

@Data
public class AlertRuleVO {

    private String ruleUuid;
    private String name;
    private String deviceUuid;
    private String ruleType;
    private String gasType;
    private String threshold;
    private int durationSeconds;
    private String severity;
    private boolean autoCreateWorkOrder;
    private boolean enabled;
    private String createdAt;
}
