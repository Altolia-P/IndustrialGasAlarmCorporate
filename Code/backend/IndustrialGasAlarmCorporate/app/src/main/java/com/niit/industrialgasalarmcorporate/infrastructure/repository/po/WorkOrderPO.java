package com.niit.industrialgasalarmcorporate.infrastructure.repository.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@TableName("t_work_order")
public class WorkOrderPO {

    @TableId
    private String workOrderUuid;

    private String title;

    private String type;

    private String description;

    private String status;

    private String priority;

    private String assignedStaffUuid;

    private String assignedStaffName;

    private String customerName;

    private String customerPhone;

    private String resolution;

    private LocalDateTime completedAt;

    private Integer version;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
