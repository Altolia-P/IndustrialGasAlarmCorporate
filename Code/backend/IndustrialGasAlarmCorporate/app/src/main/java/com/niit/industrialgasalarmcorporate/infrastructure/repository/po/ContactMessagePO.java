package com.niit.industrialgasalarmcorporate.infrastructure.repository.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@TableName("t_contact_message")
public class ContactMessagePO {

    @TableId
    private String messageUuid;

    private String name;

    private String phone;

    private String content;

    private String ip;

    private String status;

    private String processor;

    private String remark;

    private String assignedStaffUuid;

    private String assignedStaffName;

    private LocalDateTime submittedAt;

    private LocalDateTime processedAt;

    @Version
    private Integer version;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
