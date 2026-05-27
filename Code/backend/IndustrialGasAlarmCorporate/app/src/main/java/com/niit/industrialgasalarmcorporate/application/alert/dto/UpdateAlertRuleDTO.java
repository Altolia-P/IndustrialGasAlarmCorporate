package com.niit.industrialgasalarmcorporate.application.alert.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateAlertRuleDTO {

    private String name;

    private String ruleType;

    private String gasType;

    private BigDecimal threshold;

    private int durationSeconds;

    private String severity;

    private boolean autoCreateWorkOrder;
}
