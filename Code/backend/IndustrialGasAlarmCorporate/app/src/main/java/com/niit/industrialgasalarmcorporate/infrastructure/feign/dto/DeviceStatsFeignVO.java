package com.niit.industrialgasalarmcorporate.infrastructure.feign.dto;

import lombok.Data;

@Data
public class DeviceStatsFeignVO {
    private long todayDataPoints;
    private String avgConcentration;
}
