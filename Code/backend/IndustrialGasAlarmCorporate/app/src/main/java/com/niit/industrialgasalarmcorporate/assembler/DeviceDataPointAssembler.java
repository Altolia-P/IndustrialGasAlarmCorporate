package com.niit.industrialgasalarmcorporate.assembler;

import com.niit.industrialgasalarmcorporate.application.device.dto.DeviceDataPointDTO;
import com.niit.industrialgasalarmcorporate.application.device.vo.DeviceDataPointVO;
import com.niit.industrialgasalarmcorporate.domain.device.DeviceDataPoint;

import java.time.format.DateTimeFormatter;

public final class DeviceDataPointAssembler {

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private DeviceDataPointAssembler() {}

    public static DeviceDataPoint toEntity(DeviceDataPointDTO dto) {
        return new DeviceDataPoint(
                dto.getDeviceUuid(),
                dto.getTimestamp(),
                dto.getConcentration(),
                dto.getBattery(),
                dto.getTemperature(),
                dto.getHumidity(),
                dto.getSignalStrength() != null ? dto.getSignalStrength() : 0
        );
    }

    public static DeviceDataPointVO toVO(DeviceDataPoint dp) {
        DeviceDataPointVO vo = new DeviceDataPointVO();
        vo.setDeviceUuid(dp.getDeviceUuid());
        vo.setTimestamp(dp.getTimestamp().format(DTF));
        if (dp.getConcentration() != null) {
            vo.setConcentration(dp.getConcentration().toPlainString());
        }
        if (dp.getBattery() != null) {
            vo.setBattery(dp.getBattery().toPlainString());
        }
        if (dp.getTemperature() != null) {
            vo.setTemperature(dp.getTemperature().toPlainString());
        }
        if (dp.getHumidity() != null) {
            vo.setHumidity(dp.getHumidity().toPlainString());
        }
        vo.setSignalStrength(dp.getSignalStrength());
        vo.setCreatedAt(dp.getCreatedAt().format(DTF));
        return vo;
    }
}
