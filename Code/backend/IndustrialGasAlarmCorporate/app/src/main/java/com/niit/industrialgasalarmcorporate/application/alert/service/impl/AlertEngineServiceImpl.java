package com.niit.industrialgasalarmcorporate.application.alert.service.impl;

import com.niit.industrialgasalarmcorporate.application.alert.service.AlertEngineService;
import com.niit.industrialgasalarmcorporate.application.workorder.dto.CreateWorkOrderDTO;
import com.niit.industrialgasalarmcorporate.application.workorder.service.WorkOrderService;
import com.niit.industrialgasalarmcorporate.domain.alert.Alert;
import com.niit.industrialgasalarmcorporate.domain.alert.AlertRepository;
import com.niit.industrialgasalarmcorporate.domain.alert.AlertRule;
import com.niit.industrialgasalarmcorporate.domain.alert.AlertRuleRepository;
import com.niit.industrialgasalarmcorporate.domain.alert.AlertSeverity;
import com.niit.industrialgasalarmcorporate.domain.device.Device;
import com.niit.industrialgasalarmcorporate.domain.device.DeviceDataPoint;
import com.niit.industrialgasalarmcorporate.domain.device.DeviceRepository;
import com.niit.industrialgasalarmcorporate.domain.event.AlertCreatedEvent;
import com.niit.industrialgasalarmcorporate.domain.event.EventBus;
import com.niit.industrialgasalarmcorporate.infrastructure.redis.AlertSuppressRepository;
import com.niit.industrialgasalarmcorporate.infrastructure.redis.DeviceDataWindowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.ZoneOffset;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertEngineServiceImpl implements AlertEngineService {

    private final DeviceRepository deviceRepository;
    private final AlertRuleRepository alertRuleRepository;
    private final AlertRepository alertRepository;
    private final DeviceDataWindowRepository deviceDataWindowRepository;
    private final AlertSuppressRepository alertSuppressRepository;
    private final EventBus eventBus;
    private final WorkOrderService workOrderService;

    private static final Duration SUPPRESS_COOLDOWN = Duration.ofMinutes(5);

    @Override
    @Transactional
    public void evaluate(DeviceDataPoint dataPoint) {
        Device device = deviceRepository.findById(dataPoint.getDeviceUuid()).orElse(null);
        if (device == null) {
            return;
        }

        String gasType = device.getGasType().name();
        long nowSeconds = dataPoint.getTimestamp().toEpochSecond(ZoneOffset.UTC);

        var rules = alertRuleRepository.findByDeviceUuid(dataPoint.getDeviceUuid());
        rules.addAll(alertRuleRepository.findAllGlobal());

        for (AlertRule rule : rules) {
            if (!rule.matches(dataPoint.getDeviceUuid(), gasType)) {
                continue;
            }

            if (rule.getRuleType() == com.niit.industrialgasalarmcorporate.domain.alert.AlertRuleType.THRESHOLD) {
                evaluateThreshold(dataPoint, device, rule, nowSeconds);
            }
        }
    }

    private void evaluateThreshold(DeviceDataPoint dataPoint, Device device,
                                   AlertRule rule, long nowSeconds) {
        if (dataPoint.getConcentration() == null || rule.getThreshold() == null) {
            return;
        }

        boolean exceeded = rule.evaluate(dataPoint.getConcentration());

        if (exceeded) {
            deviceDataWindowRepository.addDataPoint(
                    dataPoint.getDeviceUuid(),
                    dataPoint.getConcentration().doubleValue(),
                    nowSeconds);
            long windowStart = nowSeconds - rule.getDurationSeconds();
            deviceDataWindowRepository.removeExpired(dataPoint.getDeviceUuid(), windowStart);
            long exceedCount = deviceDataWindowRepository.countExceededInWindow(
                    dataPoint.getDeviceUuid(), windowStart);

            if (exceedCount >= 1) {
                if (!alertSuppressRepository.trySuppress(
                        dataPoint.getDeviceUuid(), rule.getRuleUuid(), SUPPRESS_COOLDOWN)) {
                    return;
                }

                Alert alert = new Alert(
                        dataPoint.getDeviceUuid(),
                        rule.getRuleUuid(),
                        com.niit.industrialgasalarmcorporate.domain.alert.AlertRuleType.THRESHOLD,
                        rule.getSeverity(),
                        dataPoint.getConcentration(),
                        rule.getThreshold(),
                        String.format("设备 %s (%s) 气体浓度 %.4f 超过阈值 %.4f",
                                device.getName(), device.getGasType().name(),
                                dataPoint.getConcentration(), rule.getThreshold())
                );

                alertRepository.save(alert);

                if (rule.isAutoCreateWorkOrder()) {
                    try {
                        CreateWorkOrderDTO workOrderDTO = new CreateWorkOrderDTO();
                        workOrderDTO.setTitle(String.format("报警工单: %s - %s",
                                device.getName(), rule.getSeverity().name()));
                        workOrderDTO.setType("TECH_SUPPORT");
                        workOrderDTO.setDescription(alert.getMessage());
                        workOrderDTO.setPriority(rule.getSeverity() == AlertSeverity.CRITICAL
                                ? "HIGH" : rule.getSeverity() == AlertSeverity.WARNING ? "MEDIUM" : "LOW");
                        workOrderDTO.setCustomerName(device.getName());
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

                eventBus.publish(new AlertCreatedEvent(
                        alert.getAlertUuid(), alert.getDeviceUuid(),
                        alert.getAlertType().name(), alert.getSeverity().name(),
                        alert.getMessage()));
                log.info("Alert triggered: device={}, rule={}, concentration={}, threshold={}",
                        dataPoint.getDeviceUuid(), rule.getName(),
                        dataPoint.getConcentration(), rule.getThreshold());
            }
        }
    }
}
