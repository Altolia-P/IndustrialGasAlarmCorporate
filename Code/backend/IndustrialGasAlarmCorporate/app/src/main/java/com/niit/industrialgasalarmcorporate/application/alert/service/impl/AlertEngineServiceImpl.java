package com.niit.industrialgasalarmcorporate.application.alert.service.impl;

import com.niit.industrialgasalarmcorporate.application.alert.service.AlertEngineService;
import com.niit.industrialgasalarmcorporate.domain.alert.AlertRule;
import com.niit.industrialgasalarmcorporate.domain.alert.AlertRuleRepository;
import com.niit.industrialgasalarmcorporate.domain.alert.AlertRuleType;
import com.niit.industrialgasalarmcorporate.domain.alert.AlertSeverity;
import com.niit.industrialgasalarmcorporate.domain.device.Device;
import com.niit.industrialgasalarmcorporate.domain.device.DeviceRepository;
import com.niit.industrialgasalarmcorporate.infrastructure.config.RabbitMQConfig;
import com.niit.industrialgasalarmcorporate.infrastructure.mq.AlertMessage;
import com.niit.industrialgasalarmcorporate.infrastructure.redis.AlertSuppressRepository;
import com.niit.industrialgasalarmcorporate.infrastructure.redis.DeviceDataWindowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertEngineServiceImpl implements AlertEngineService {

    private final DeviceRepository deviceRepository;
    private final AlertRuleRepository alertRuleRepository;
    private final DeviceDataWindowRepository deviceDataWindowRepository;
    private final AlertSuppressRepository alertSuppressRepository;
    private final RabbitTemplate rabbitTemplate;

    private static final Duration SUPPRESS_COOLDOWN = Duration.ofMinutes(5);

    @Override
    @Transactional
    public void evaluate(String deviceUuid, BigDecimal concentration, LocalDateTime timestamp) {
        Device device = deviceRepository.findById(deviceUuid).orElse(null);
        if (device == null) {
            return;
        }

        if (device.getGasType() == null) {
            log.warn("设备 {} 的气体类型为空，跳过告警评估", deviceUuid);
            return;
        }
        String gasType = device.getGasType().name();
        long nowSeconds = timestamp.toEpochSecond(ZoneOffset.UTC);

        var rules = alertRuleRepository.findByDeviceUuid(deviceUuid);
        if (rules == null) {
            rules = new java.util.ArrayList<>();
        }
        var globalRules = alertRuleRepository.findAllGlobal();
        if (globalRules != null) {
            rules.addAll(globalRules);
        }

        for (AlertRule rule : rules) {
            if (!rule.matches(deviceUuid, gasType)) {
                continue;
            }

            if (rule.getRuleType() == AlertRuleType.THRESHOLD) {
                evaluateThreshold(deviceUuid, concentration, timestamp, device, rule, nowSeconds);
            }
        }
    }

    private void evaluateThreshold(String deviceUuid, BigDecimal concentration, LocalDateTime timestamp,
                                   Device device, AlertRule rule, long nowSeconds) {
        if (concentration == null || rule.getThreshold() == null) {
            return;
        }

        boolean exceeded = rule.evaluate(concentration);

        if (exceeded) {
            long windowStart = nowSeconds - rule.getDurationSeconds();
            String ruleUuid = rule.getRuleUuid();

            if (!alertSuppressRepository.trySuppress(deviceUuid, ruleUuid, SUPPRESS_COOLDOWN)) {
                return;
            }

            deviceDataWindowRepository.removeExpired(deviceUuid, ruleUuid, windowStart);
            deviceDataWindowRepository.addDataPoint(
                    deviceUuid,
                    ruleUuid,
                    concentration.doubleValue(),
                    nowSeconds);
            long exceedCount = deviceDataWindowRepository.countExceededInWindow(
                    deviceUuid, ruleUuid, windowStart);

            if (exceedCount >= 1) {
                AlertMessage message = new AlertMessage();
                message.setDeviceUuid(deviceUuid);
                message.setRuleUuid(rule.getRuleUuid());
                message.setAlertType(AlertRuleType.THRESHOLD.name());
                message.setSeverity(rule.getSeverity().name());
                message.setConcentration(concentration);
                message.setThreshold(rule.getThreshold());
                message.setMessage(String.format("设备 %s (%s) 气体浓度 %.4f 超过阈值 %.4f",
                        device.getName(), device.getGasType().name(),
                        concentration, rule.getThreshold()));
                message.setAutoCreateWorkOrder(rule.isAutoCreateWorkOrder());
                message.setDeviceName(device.getName());

                rabbitTemplate.convertAndSend(
                        RabbitMQConfig.ALERT_EXCHANGE,
                        RabbitMQConfig.ALERT_ROUTING_KEY,
                        message);

                log.info("告警消息已发送到 RabbitMQ: device={}, rule={}, concentration={}, threshold={}",
                        deviceUuid, rule.getName(),
                        concentration, rule.getThreshold());
            }
        }
    }
}
