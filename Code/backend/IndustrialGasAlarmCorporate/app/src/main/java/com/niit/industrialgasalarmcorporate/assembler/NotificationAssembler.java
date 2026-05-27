package com.niit.industrialgasalarmcorporate.assembler;

import com.niit.industrialgasalarmcorporate.application.notification.vo.NotificationVO;
import com.niit.industrialgasalarmcorporate.domain.notification.Notification;

import java.time.format.DateTimeFormatter;

public final class NotificationAssembler {

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private NotificationAssembler() {}

    public static NotificationVO toVO(Notification notification) {
        NotificationVO vo = new NotificationVO();
        vo.setNotificationUuid(notification.getNotificationUuid());
        vo.setAlertUuid(notification.getAlertUuid());
        vo.setRecipient(notification.getRecipient());
        vo.setChannel(notification.getChannel().name());
        vo.setContent(notification.getContent());
        vo.setStatus(notification.getStatus().name());
        vo.setRetryCount(notification.getRetryCount());
        vo.setErrorMessage(notification.getErrorMessage());
        if (notification.getSentAt() != null) {
            vo.setSentAt(notification.getSentAt().format(DTF));
        }
        vo.setCreatedAt(notification.getCreatedAt().format(DTF));
        return vo;
    }
}
