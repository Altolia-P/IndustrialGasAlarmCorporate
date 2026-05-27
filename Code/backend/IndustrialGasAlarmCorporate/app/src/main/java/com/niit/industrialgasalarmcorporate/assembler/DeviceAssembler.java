package com.niit.industrialgasalarmcorporate.assembler;

import com.niit.industrialgasalarmcorporate.application.device.dto.CreateDeviceDTO;
import com.niit.industrialgasalarmcorporate.application.device.dto.UpdateDeviceDTO;
import com.niit.industrialgasalarmcorporate.application.device.vo.DeviceVO;
import com.niit.industrialgasalarmcorporate.domain.device.Device;
import com.niit.industrialgasalarmcorporate.domain.device.GasType;

import java.time.format.DateTimeFormatter;

public final class DeviceAssembler {

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private DeviceAssembler() {}

    public static Device toEntity(CreateDeviceDTO dto) {
        return new Device(
                dto.getSerialNumber(),
                dto.getModel(),
                dto.getName(),
                dto.getCustomerUuid(),
                GasType.valueOf(dto.getGasType()),
                dto.getInstallLocation(),
                dto.getRangeMin(),
                dto.getRangeMax(),
                dto.getAlertThreshold()
        );
    }

    public static void updateEntity(Device device, UpdateDeviceDTO dto) {
        device.update(
                dto.getName(),
                dto.getModel(),
                dto.getCustomerUuid(),
                dto.getInstallLocation(),
                dto.getGasType() != null ? GasType.valueOf(dto.getGasType()) : null,
                dto.getRangeMin(),
                dto.getRangeMax(),
                dto.getAlertThreshold()
        );
    }

    public static DeviceVO toVO(Device device) {
        DeviceVO vo = new DeviceVO();
        vo.setDeviceUuid(device.getDeviceUuid());
        vo.setSerialNumber(device.getSerialNumber());
        vo.setName(device.getName());
        vo.setModel(device.getModel());
        vo.setCustomerUuid(device.getCustomerUuid());
        vo.setInstallLocation(device.getInstallLocation());
        if (device.getInstallDate() != null) {
            vo.setInstallDate(device.getInstallDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
        }
        vo.setGasType(device.getGasType().name());
        if (device.getRangeMin() != null) {
            vo.setRangeMin(device.getRangeMin().toPlainString());
        }
        if (device.getRangeMax() != null) {
            vo.setRangeMax(device.getRangeMax().toPlainString());
        }
        if (device.getAlertThreshold() != null) {
            vo.setAlertThreshold(device.getAlertThreshold().toPlainString());
        }
        vo.setStatus(device.getStatus().name());
        vo.setCreatedAt(device.getCreatedAt().format(DTF));
        return vo;
    }
}
