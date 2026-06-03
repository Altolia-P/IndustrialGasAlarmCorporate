package com.niit.industrialgasalarmcorporate.application.alert.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateAlertRuleDTO {

    @NotBlank(message = "规则名称不能为空")
    @Size(max = 100, message = "规则名称不超过100字符")
    private String name;

    @Size(max = 36, message = "设备UUID不超过36字符")
    private String deviceUuid;

    @NotBlank(message = "规则类型不能为空")
    @Size(max = 50, message = "规则类型不超过50字符")
    private String ruleType;

    @Size(max = 50, message = "气体类型不超过50字符")
    private String gasType;

    private BigDecimal threshold;

    @Min(value = 10, message = "持续时间至少10秒")
    private int durationSeconds = 60;

    @NotBlank(message = "严重程度不能为空")
    @Size(max = 20, message = "严重程度不超过20字符")
    private String severity;

    private Boolean autoCreateWorkOrder;
}
