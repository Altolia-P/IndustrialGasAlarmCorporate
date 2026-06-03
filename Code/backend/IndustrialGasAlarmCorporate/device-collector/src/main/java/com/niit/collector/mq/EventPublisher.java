package com.niit.collector.mq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventPublisher {

    private static final String ALERT_EXCHANGE = "alert.exchange";
    private static final String ALERT_EVALUATE_KEY = "alert.evaluate";
    private static final String DEVICE_EXCHANGE = "device.exchange";
    private static final String DEVICE_ONLINE_KEY = "device.online";
    private static final String DEVICE_OFFLINE_KEY = "device.offline";

    private final RabbitTemplate rabbitTemplate;

    public void publishAlertEvaluate(String deviceUuid, String concentration, String timestamp) {
        Map<String, Object> msg = new LinkedHashMap<>();
        msg.put("type", "alert.evaluate");
        msg.put("deviceUuid", deviceUuid);
        msg.put("concentration", concentration);
        msg.put("timestamp", timestamp);
        rabbitTemplate.convertAndSend(ALERT_EXCHANGE, ALERT_EVALUATE_KEY, msg);
        log.debug("已发送告警评估消息: deviceUuid={}", deviceUuid);
    }

    public void publishDeviceOnline(String deviceUuid) {
        Map<String, Object> msg = new LinkedHashMap<>();
        msg.put("type", "device.online");
        msg.put("deviceUuid", deviceUuid);
        rabbitTemplate.convertAndSend(DEVICE_EXCHANGE, DEVICE_ONLINE_KEY, msg);
        log.debug("已发送设备上线消息: deviceUuid={}", deviceUuid);
    }

    public void publishDeviceOffline(String deviceUuid) {
        Map<String, Object> msg = new LinkedHashMap<>();
        msg.put("type", "device.offline");
        msg.put("deviceUuid", deviceUuid);
        rabbitTemplate.convertAndSend(DEVICE_EXCHANGE, DEVICE_OFFLINE_KEY, msg);
        log.debug("已发送设备离线消息: deviceUuid={}", deviceUuid);
    }
}
