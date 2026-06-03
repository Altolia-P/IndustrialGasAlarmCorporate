package com.niit.industrialgasalarmcorporate.infrastructure.feign.dto;

import lombok.Data;

@Data
public class DeviceDataPointFeignVO {

    private String deviceUuid;
    private String timestamp;
    private String concentration;
    private String battery;
    private String temperature;
    private String humidity;
    private Integer signalStrength;
    private String createdAt;
}
