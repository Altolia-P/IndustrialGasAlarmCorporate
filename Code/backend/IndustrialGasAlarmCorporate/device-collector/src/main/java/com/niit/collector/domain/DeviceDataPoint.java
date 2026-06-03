package com.niit.collector.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class DeviceDataPoint {

    private final String dataPointId;
    private final String deviceUuid;
    private final LocalDateTime timestamp;
    private final BigDecimal concentration;
    private final BigDecimal battery;
    private final BigDecimal temperature;
    private final BigDecimal humidity;
    private final int signalStrength;
    private final LocalDateTime createdAt;

    public DeviceDataPoint(String deviceUuid, LocalDateTime timestamp, BigDecimal concentration,
                           BigDecimal battery, BigDecimal temperature, BigDecimal humidity,
                           int signalStrength) {
        this.dataPointId = UUID.randomUUID().toString();
        this.deviceUuid = deviceUuid;
        this.timestamp = timestamp;
        this.concentration = concentration;
        this.battery = battery;
        this.temperature = temperature;
        this.humidity = humidity;
        this.signalStrength = signalStrength;
        this.createdAt = LocalDateTime.now();
    }

    public DeviceDataPoint(String dataPointId, String deviceUuid, LocalDateTime timestamp,
                           BigDecimal concentration, BigDecimal battery, BigDecimal temperature,
                           BigDecimal humidity, int signalStrength, LocalDateTime createdAt) {
        this.dataPointId = dataPointId;
        this.deviceUuid = deviceUuid;
        this.timestamp = timestamp;
        this.concentration = concentration;
        this.battery = battery;
        this.temperature = temperature;
        this.humidity = humidity;
        this.signalStrength = signalStrength;
        this.createdAt = createdAt;
    }

    public String getDataPointId() { return dataPointId; }
    public String getDeviceUuid() { return deviceUuid; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public BigDecimal getConcentration() { return concentration; }
    public BigDecimal getBattery() { return battery; }
    public BigDecimal getTemperature() { return temperature; }
    public BigDecimal getHumidity() { return humidity; }
    public int getSignalStrength() { return signalStrength; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
