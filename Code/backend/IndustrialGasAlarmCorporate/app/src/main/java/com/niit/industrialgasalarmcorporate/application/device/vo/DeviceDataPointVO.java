package com.niit.industrialgasalarmcorporate.application.device.vo;

import lombok.Data;

@Data
public class DeviceDataPointVO {

    private String deviceUuid;
    private String timestamp;
    private String concentration;
    private String battery;
    private String temperature;
    private String humidity;
    private Integer signalStrength;
    private String createdAt;
}
