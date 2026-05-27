package com.niit.industrialgasalarmcorporate.infrastructure.repository.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@TableName("t_alert")
public class AlertPO {

    @TableId
    private String alertUuid;

    private String deviceUuid;

    private String ruleUuid;

    private String alertType;

    private String severity;

    private BigDecimal concentration;

    private BigDecimal threshold;

    private String message;

    private String status;

    private LocalDateTime triggeredAt;

    private LocalDateTime confirmedAt;

    private String confirmedBy;

    private LocalDateTime resolvedAt;

    private String resolvedBy;

    private String workOrderUuid;

    @Version
    private Integer version;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
