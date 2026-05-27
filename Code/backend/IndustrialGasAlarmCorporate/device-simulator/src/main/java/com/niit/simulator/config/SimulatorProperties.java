package com.niit.simulator.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "simulator")
public class SimulatorProperties {

    private String targetUrl = "http://localhost:8080/api/v1/public/device-data";
    private long intervalMs = 5000;
    private boolean enabled = true;

    private List<DeviceConfig> devices = List.of();

    @Data
    public static class DeviceConfig {
        private String deviceUuid;
        private String gasType = "CH4";
        private Range concentration = new Range();
        private Range battery = new Range();
        private Range temperature = new Range();
        private Range humidity = new Range();
    }

    @Data
    public static class Range {
        private BigDecimal min = BigDecimal.ZERO;
        private BigDecimal max = BigDecimal.TEN;
    }
}
