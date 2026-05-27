package com.niit.industrialgasalarmcorporate.infrastructure.repository.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@TableName("t_alert_rule")
public class AlertRulePO {

    @TableId
    private String ruleUuid;

    private String name;

    private String deviceUuid;

    private String ruleType;

    private String gasType;

    private BigDecimal threshold;

    private Integer durationSeconds;

    private String severity;

    private Integer autoCreateWorkOrder;

    private Integer enabled;

    @Version
    private Integer version;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
