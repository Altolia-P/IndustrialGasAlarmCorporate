package com.niit.industrialgasalarmcorporate.application.notification.service.impl;

import com.niit.industrialgasalarmcorporate.application.notification.service.NotificationService;
import com.niit.industrialgasalarmcorporate.application.notification.vo.NotificationVO;
import com.niit.industrialgasalarmcorporate.assembler.NotificationAssembler;
import com.niit.industrialgasalarmcorporate.common.base.Page;
import com.niit.industrialgasalarmcorporate.domain.event.AlertCreatedEvent;
import com.niit.industrialgasalarmcorporate.domain.notification.Notification;
import com.niit.industrialgasalarmcorporate.domain.notification.NotificationChannel;
import com.niit.industrialgasalarmcorporate.domain.notification.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    @Transactional
    public void notifyAlert(AlertCreatedEvent event) {
        Notification notification = new Notification(
                event.getAlertUuid(),
                "system",
                NotificationChannel.IN_APP,
                buildContent(event)
        );
        notification.markSent();
        notificationRepository.save(notification);
        log.info("站内通知已发送: notificationUuid={}, alertUuid={}",
                notification.getNotificationUuid(), event.getAlertUuid());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationVO> findByAlertUuid(String alertUuid, int page, int size) {
        Page<Notification> domainPage = notificationRepository.findByAlertUuid(alertUuid, page, size);
        return new Page<>(
                domainPage.getContent().stream().map(NotificationAssembler::toVO)
                        .collect(Collectors.toList()),
                domainPage.getTotalElements(),
                domainPage.getSize(),
                domainPage.getNumber()
        );
    }

    private String buildContent(AlertCreatedEvent event) {
        return String.format("[%s][%s] 设备 %s 触发报警：%s",
                event.getSeverity(), event.getAlertType(),
                event.getDeviceUuid(), event.getMessage());
    }
}
