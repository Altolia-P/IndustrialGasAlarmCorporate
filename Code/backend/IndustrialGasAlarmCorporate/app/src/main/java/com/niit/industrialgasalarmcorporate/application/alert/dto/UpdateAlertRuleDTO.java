package com.niit.industrialgasalarmcorporate.application.alert.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateAlertRuleDTO {

    @Size(max = 100, message = "规则名称不超过100字符")
    private String name;

    @Size(max = 50, message = "规则类型不超过50字符")
    private String ruleType;

    @Size(max = 50, message = "气体类型不超过50字符")
    private String gasType;

    private BigDecimal threshold;

    private int durationSeconds;

    @Size(max = 20, message = "严重程度不超过20字符")
    private String severity;

    private Boolean autoCreateWorkOrder;
}
