package com.niit.industrialgasalarmcorporate.infrastructure.event;

import com.niit.industrialgasalarmcorporate.application.notification.service.NotificationService;
import com.niit.industrialgasalarmcorporate.domain.event.AlertCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class AlertCreatedEventListener {

    private final NotificationService notificationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(AlertCreatedEvent event) {
        log.info("收到报警创建事件: alertUuid={}, deviceUuid={}, severity={}",
                event.getAlertUuid(), event.getDeviceUuid(), event.getSeverity());
        notificationService.notifyAlert(event);
    }
}
