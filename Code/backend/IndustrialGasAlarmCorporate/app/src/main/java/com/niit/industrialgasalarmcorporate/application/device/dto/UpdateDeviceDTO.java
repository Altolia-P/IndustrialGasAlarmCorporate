package com.niit.industrialgasalarmcorporate.application.device.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateDeviceDTO {

    private String name;

    private String model;

    private String customerUuid;

    private String installLocation;

    private String gasType;

    private BigDecimal rangeMin;

    private BigDecimal rangeMax;

    private BigDecimal alertThreshold;
}
