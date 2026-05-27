package com.niit.industrialgasalarmcorporate.infrastructure.repository.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@TableName("t_notification")
public class NotificationPO {

    @TableId
    private String notificationUuid;

    private String alertUuid;

    private String recipient;

    private String channel;

    private String content;

    private String status;

    private Integer retryCount;

    private String errorMessage;

    private LocalDateTime sentAt;

    @Version
    private Integer version;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
