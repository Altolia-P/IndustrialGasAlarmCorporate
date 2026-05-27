package com.niit.simulator.scheduler;

import com.niit.simulator.config.SimulatorProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "simulator.enabled", havingValue = "true", matchIfMissing = true)
public class SimulationScheduler {

    private final SimulatorProperties properties;
    private final RestTemplate restTemplate = new RestTemplate();
    private final Random random = new Random();

    @Scheduled(fixedDelayString = "${simulator.interval-ms:5000}")
    public void generateData() {
        if (!properties.isEnabled() || properties.getDevices().isEmpty()) {
            return;
        }

        for (SimulatorProperties.DeviceConfig device : properties.getDevices()) {
            try {
                Map<String, Object> payload = buildPayload(device);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

                restTemplate.postForEntity(properties.getTargetUrl(), request, String.class);
                log.debug("模拟数据已上报: device={}, concentration={}",
                        device.getDeviceUuid(), payload.get("concentration"));
            } catch (Exception e) {
                log.warn("模拟数据上报失败: device={}, error={}",
                        device.getDeviceUuid(), e.getMessage());
            }
        }
    }

    private Map<String, Object> buildPayload(SimulatorProperties.DeviceConfig device) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("deviceUuid", device.getDeviceUuid());
        payload.put("timestamp", LocalDateTime.now().toString());
        payload.put("concentration", randomRange(device.getConcentration()));
        payload.put("battery", randomRange(device.getBattery()));
        payload.put("temperature", randomRange(device.getTemperature()));
        payload.put("humidity", randomRange(device.getHumidity()));
        payload.put("signalStrength", 70 + random.nextInt(31));
        return payload;
    }

    private BigDecimal randomRange(SimulatorProperties.Range range) {
        double min = range.getMin().doubleValue();
        double max = range.getMax().doubleValue();
        double value = min + random.nextDouble() * (max - min);
        return BigDecimal.valueOf(value).setScale(4, RoundingMode.HALF_UP);
    }
}
