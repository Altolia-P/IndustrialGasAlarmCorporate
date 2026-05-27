package com.niit.industrialgasalarmcorporate.application.device.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DeviceDataPointDTO {

    @NotBlank(message = "设备UUID不能为空")
    private String deviceUuid;

    @NotNull(message = "时间戳不能为空")
    private LocalDateTime timestamp;

    @NotNull(message = "浓度值不能为空")
    private BigDecimal concentration;

    private BigDecimal battery;

    private BigDecimal temperature;

    private BigDecimal humidity;

    private Integer signalStrength;
}
