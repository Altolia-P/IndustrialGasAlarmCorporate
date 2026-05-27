package com.niit.industrialgasalarmcorporate.domain.notification;

import java.time.LocalDateTime;
import java.util.UUID;

public class Notification {

    private final String notificationUuid;
    private final String alertUuid;
    private String recipient;
    private NotificationChannel channel;
    private String content;
    private NotificationStatus status;
    private int retryCount;
    private String errorMessage;
    private LocalDateTime sentAt;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Notification(String alertUuid, String recipient, NotificationChannel channel,
                        String content) {
        this.notificationUuid = UUID.randomUUID().toString();
        this.alertUuid = alertUuid;
        this.recipient = recipient;
        this.channel = channel;
        this.content = content;
        this.status = NotificationStatus.PENDING;
        this.retryCount = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public Notification(String notificationUuid, String alertUuid, String recipient,
                        NotificationChannel channel, String content, NotificationStatus status,
                        int retryCount, String errorMessage, LocalDateTime sentAt,
                        LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.notificationUuid = notificationUuid;
        this.alertUuid = alertUuid;
        this.recipient = recipient;
        this.channel = channel;
        this.content = content;
        this.status = status;
        this.retryCount = retryCount;
        this.errorMessage = errorMessage;
        this.sentAt = sentAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void markSent() {
        this.status = NotificationStatus.SENT;
        this.sentAt = LocalDateTime.now();
    }

    public void markFailed(String errorMessage) {
        this.status = NotificationStatus.FAILED;
        this.retryCount++;
        this.errorMessage = errorMessage;
    }

    public void markDelivered() {
        this.status = NotificationStatus.DELIVERED;
    }

    public String getNotificationUuid() { return notificationUuid; }
    public String getAlertUuid() { return alertUuid; }
    public String getRecipient() { return recipient; }
    public NotificationChannel getChannel() { return channel; }
    public String getContent() { return content; }
    public NotificationStatus getStatus() { return status; }
    public int getRetryCount() { return retryCount; }
    public String getErrorMessage() { return errorMessage; }
    public LocalDateTime getSentAt() { return sentAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
