package com.niit.industrialgasalarmcorporate.application.device.service;

import com.niit.industrialgasalarmcorporate.application.device.dto.CreateDeviceDTO;
import com.niit.industrialgasalarmcorporate.application.device.dto.UpdateDeviceDTO;
import com.niit.industrialgasalarmcorporate.application.device.vo.DeviceVO;
import com.niit.industrialgasalarmcorporate.common.base.Page;

public interface DeviceService {

    Page<DeviceVO> findDevices(String customerUuid, String model, String gasType,
                               String status, int page, int size);

    DeviceVO getDevice(String deviceUuid);

    DeviceVO createDevice(CreateDeviceDTO dto);

    DeviceVO updateDevice(String deviceUuid, UpdateDeviceDTO dto);

    void deleteDevice(String deviceUuid);

    void markAbnormal(String deviceUuid);

    void markNormal(String deviceUuid);

    void markOffline(String deviceUuid);

    void startMaintenance(String deviceUuid);

    void endMaintenance(String deviceUuid);
}
