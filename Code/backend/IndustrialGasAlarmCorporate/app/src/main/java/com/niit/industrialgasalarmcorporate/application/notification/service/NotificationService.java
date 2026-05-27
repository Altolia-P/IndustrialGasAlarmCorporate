package com.niit.industrialgasalarmcorporate.application.notification.service;

import com.niit.industrialgasalarmcorporate.application.notification.vo.NotificationVO;
import com.niit.industrialgasalarmcorporate.common.base.Page;
import com.niit.industrialgasalarmcorporate.domain.event.AlertCreatedEvent;

public interface NotificationService {

    void notifyAlert(AlertCreatedEvent event);

    Page<NotificationVO> findByAlertUuid(String alertUuid, int page, int size);
}
