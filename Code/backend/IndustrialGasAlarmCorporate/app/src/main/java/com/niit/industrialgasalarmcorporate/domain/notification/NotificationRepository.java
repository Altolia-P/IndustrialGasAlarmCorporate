package com.niit.industrialgasalarmcorporate.domain.notification;

import com.niit.industrialgasalarmcorporate.common.base.Page;

import java.util.Optional;

public interface NotificationRepository {

    void save(Notification notification);

    Optional<Notification> findById(String notificationUuid);

    Page<Notification> findByAlertUuid(String alertUuid, int page, int size);
}
