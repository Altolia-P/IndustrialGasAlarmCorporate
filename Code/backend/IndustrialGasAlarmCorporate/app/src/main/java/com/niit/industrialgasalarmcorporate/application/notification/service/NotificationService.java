package com.niit.industrialgasalarmcorporate.application.notification.service;

import com.niit.industrialgasalarmcorporate.application.notification.vo.NotificationVO;
import com.niit.industrialgasalarmcorporate.common.base.Page;
import com.niit.industrialgasalarmcorporate.domain.event.AlertCreatedEvent;

public interface NotificationService {

    void notifyAlert(AlertCreatedEvent event);

    Page<NotificationVO> findByAlertUuid(String alertUuid, int page, int size);

    Page<NotificationVO> listAll(int page, int size);

    void resend(String notificationUuid);

    long getUnreadCount(java.time.LocalDateTime since);

    java.util.List<NotificationVO> getRecent(int limit);
}
