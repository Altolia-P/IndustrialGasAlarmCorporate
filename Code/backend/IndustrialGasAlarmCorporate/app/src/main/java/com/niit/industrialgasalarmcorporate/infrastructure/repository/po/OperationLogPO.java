package com.niit.industrialgasalarmcorporate.infrastructure.repository.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@TableName("t_operation_log")
public class OperationLogPO {

    @TableId
    private String logId;

    private String operatorUuid;

    private String operatorName;

    private String operation;

    private String targetType;

    private String targetId;

    private String targetName;

    private String detail;

    private String businessPurpose;

    private String ip;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
