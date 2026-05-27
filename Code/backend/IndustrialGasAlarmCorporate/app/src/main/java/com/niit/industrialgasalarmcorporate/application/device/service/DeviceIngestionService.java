package com.niit.industrialgasalarmcorporate.application.device.service;

import com.niit.industrialgasalarmcorporate.application.device.dto.DeviceDataPointDTO;
import com.niit.industrialgasalarmcorporate.application.device.vo.DeviceDataPointVO;

import java.util.List;

public interface DeviceIngestionService {

    void ingest(DeviceDataPointDTO dto);

    List<DeviceDataPointVO> getDataPoints(String deviceUuid, String from, String to);

    DeviceDataPointVO getLatest(String deviceUuid);
}
