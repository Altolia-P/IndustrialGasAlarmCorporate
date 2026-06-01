package com.niit.industrialgasalarmcorporate.application.device.vo;

import lombok.Data;

@Data
public class DeviceVO {

    private String deviceUuid;
    private String serialNumber;
    private String name;
    private String model;
    private String customerUuid;
    private String installLocation;
    private String installDate;
    private String gasType;
    private String rangeMin;
    private String rangeMax;
    private String alertThreshold;
    private String status;
    private String createdAt;

    private String customerName;
    private String customerPhone;
}
