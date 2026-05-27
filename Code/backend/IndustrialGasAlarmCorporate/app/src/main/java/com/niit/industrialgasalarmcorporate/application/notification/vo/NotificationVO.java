package com.niit.industrialgasalarmcorporate.application.notification.vo;

import lombok.Data;

@Data
public class NotificationVO {

    private String notificationUuid;
    private String alertUuid;
    private String recipient;
    private String channel;
    private String content;
    private String status;
    private int retryCount;
    private String errorMessage;
    private String sentAt;
    private String createdAt;
}
