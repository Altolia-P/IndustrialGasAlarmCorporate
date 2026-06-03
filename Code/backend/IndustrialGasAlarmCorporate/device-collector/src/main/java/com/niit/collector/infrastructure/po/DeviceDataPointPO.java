package com.niit.collector.infrastructure.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@TableName("t_device_data_point")
public class DeviceDataPointPO {

    @TableId
    private String dataPointId;

    private String deviceUuid;

    private LocalDateTime recordedAt;

    private BigDecimal concentration;

    private BigDecimal battery;

    private BigDecimal temperature;

    private BigDecimal humidity;

    private Integer signalStrength;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
