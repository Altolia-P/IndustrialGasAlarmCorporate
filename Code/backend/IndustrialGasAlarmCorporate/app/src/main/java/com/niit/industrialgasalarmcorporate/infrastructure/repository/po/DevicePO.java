package com.niit.industrialgasalarmcorporate.infrastructure.repository.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@TableName("t_device")
public class DevicePO {

    @TableId
    private String deviceUuid;

    private String serialNumber;

    private String name;

    private String model;

    private String customerUuid;

    private String installLocation;

    private LocalDate installDate;

    private String gasType;

    private BigDecimal rangeMin;

    private BigDecimal rangeMax;

    private BigDecimal alertThreshold;

    private String status;

    @Version
    private Integer version;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
