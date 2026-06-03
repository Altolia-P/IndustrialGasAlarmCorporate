package com.niit.industrialgasalarmcorporate.infrastructure.mq;

import com.niit.industrialgasalarmcorporate.application.notification.service.NotificationService;
import com.niit.industrialgasalarmcorporate.application.workorder.dto.CreateWorkOrderDTO;
import com.niit.industrialgasalarmcorporate.application.workorder.service.WorkOrderService;
import com.niit.industrialgasalarmcorporate.domain.alert.Alert;
import com.niit.industrialgasalarmcorporate.domain.alert.AlertRepository;
import com.niit.industrialgasalarmcorporate.domain.alert.AlertRuleType;
import com.niit.industrialgasalarmcorporate.domain.alert.AlertSeverity;
import com.niit.industrialgasalarmcorporate.domain.event.AlertCreatedEvent;
import com.niit.industrialgasalarmcorporate.infrastructure.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlertConsumer {

    private final AlertRepository alertRepository;
    private final WorkOrderService workOrderService;
    private final NotificationService notificationService;

    @RabbitListener(queues = RabbitMQConfig.ALERT_QUEUE,
            containerFactory = "alertListenerContainerFactory")
    @Transactional
    public void handleAlert(AlertMessage message) {
        log.info("收到告警消息: deviceUuid={}, severity={}, concentration={}",
                message.getDeviceUuid(), message.getSeverity(), message.getConcentration());

        AlertSeverity severity = AlertSeverity.valueOf(message.getSeverity());
        AlertRuleType alertType = AlertRuleType.valueOf(message.getAlertType());

        Alert alert = new Alert(
                message.getDeviceUuid(),
                message.getRuleUuid(),
                alertType,
                severity,
                message.getConcentration(),
                message.getThreshold(),
                message.getMessage()
        );

        alertRepository.save(alert);

        if (message.isAutoCreateWorkOrder()) {
            try {
                CreateWorkOrderDTO workOrderDTO = new CreateWorkOrderDTO();
                workOrderDTO.setTitle(String.format("报警工单: %s - %s",
                        message.getDeviceName(), message.getSeverity()));
                workOrderDTO.setType("TECH_SUPPORT");
                workOrderDTO.setDescription(alert.getMessage());
                workOrderDTO.setPriority(severity == AlertSeverity.CRITICAL
                        ? "HIGH" : severity == AlertSeverity.WARNING ? "MEDIUM" : "LOW");
                workOrderDTO.setCustomerName(message.getDeviceName());
                String workOrderUuid = workOrderService.createWorkOrder(workOrderDTO)
                        .getWorkOrderUuid();
                alert.setWorkOrderUuid(workOrderUuid);
                alertRepository.save(alert);
                log.info("工单自动创建: alertUuid={}, workOrderUuid={}",
                        alert.getAlertUuid(), workOrderUuid);
            } catch (Exception e) {
                log.warn("自动创建工单失败: alertUuid={}, error={}",
                        alert.getAlertUuid(), e.getMessage());
            }
        }

        notificationService.notifyAlert(new AlertCreatedEvent(
                alert.getAlertUuid(), alert.getDeviceUuid(),
                alert.getAlertType().name(), alert.getSeverity().name(),
                alert.getMessage()));

        log.info("告警处理完成: alertUuid={}", alert.getAlertUuid());
    }
}
