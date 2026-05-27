package com.niit.industrialgasalarmcorporate.application.device.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateDeviceDTO {

    @NotBlank(message = "序列号不能为空")
    private String serialNumber;

    @NotBlank(message = "设备名称不能为空")
    private String name;

    @NotBlank(message = "设备型号不能为空")
    private String model;

    @NotBlank(message = "所属客户不能为空")
    private String customerUuid;

    @NotBlank(message = "气体类型不能为空")
    private String gasType;

    private String installLocation;

    private BigDecimal rangeMin;

    private BigDecimal rangeMax;

    private BigDecimal alertThreshold;
}
