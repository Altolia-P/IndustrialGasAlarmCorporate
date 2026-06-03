package com.niit.industrialgasalarmcorporate.infrastructure.mq;

import com.niit.industrialgasalarmcorporate.application.alert.service.AlertEngineService;
import com.niit.industrialgasalarmcorporate.infrastructure.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlertEvaluateConsumer {

    private final AlertEngineService alertEngineService;

    @RabbitListener(queues = RabbitMQConfig.ALERT_EVALUATE_QUEUE)
    public void handleAlertEvaluate(Map<String, Object> msg) {
        String deviceUuid = (String) msg.get("deviceUuid");
        String concentration = (String) msg.get("concentration");
        String timestamp = (String) msg.get("timestamp");

        if (deviceUuid == null || concentration == null || timestamp == null) {
            log.warn("告警评估消息缺少必要字段: {}", msg);
            return;
        }

        try {
            alertEngineService.evaluate(
                    deviceUuid,
                    new BigDecimal(concentration),
                    LocalDateTime.parse(timestamp));
        } catch (Exception e) {
            log.error("告警评估失败: deviceUuid={}, error={}", deviceUuid, e.getMessage(), e);
        }
    }
}
