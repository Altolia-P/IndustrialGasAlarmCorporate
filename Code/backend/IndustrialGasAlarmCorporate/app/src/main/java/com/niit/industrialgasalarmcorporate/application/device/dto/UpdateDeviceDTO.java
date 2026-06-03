package com.niit.industrialgasalarmcorporate.application.device.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateDeviceDTO {

    @Size(max = 100, message = "设备名称不超过100字符")
    private String name;

    @Size(max = 100, message = "设备型号不超过100字符")
    private String model;

    @Size(max = 36, message = "客户UUID不超过36字符")
    private String customerUuid;

    @Size(max = 200, message = "安装位置不超过200字符")
    private String installLocation;

    @Size(max = 50, message = "气体类型不超过50字符")
    private String gasType;

    private BigDecimal rangeMin;

    private BigDecimal rangeMax;

    private BigDecimal alertThreshold;
}
