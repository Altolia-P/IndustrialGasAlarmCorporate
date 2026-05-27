package com.niit.industrialgasalarmcorporate.application.alert.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateAlertRuleDTO {

    @NotBlank(message = "规则名称不能为空")
    private String name;

    private String deviceUuid;

    @NotBlank(message = "规则类型不能为空")
    private String ruleType;

    private String gasType;

    private BigDecimal threshold;

    @Min(value = 10, message = "持续时间至少10秒")
    private int durationSeconds = 60;

    @NotBlank(message = "严重程度不能为空")
    private String severity;

    private boolean autoCreateWorkOrder;
}
