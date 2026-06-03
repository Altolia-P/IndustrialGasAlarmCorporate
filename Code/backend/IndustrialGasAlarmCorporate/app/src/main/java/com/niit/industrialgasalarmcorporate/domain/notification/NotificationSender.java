package com.niit.industrialgasalarmcorporate.domain.notification;

/**
 * 通知发送器接口 — 每种外部渠道实现一个 Sender
 */
public interface NotificationSender {

    /**
     * 发送通知
     *
     * @param notification 待发送的通知
     * @return true 表示发送成功，false 表示发送失败（可触发降级）
     */
    boolean send(Notification notification);

    /**
     * 当前 Sender 支持的渠道
     */
    NotificationChannel supportedChannel();
}
