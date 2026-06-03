package com.niit.collector.service;

import com.niit.collector.dto.DeviceDataPointDTO;
import com.niit.collector.vo.DeviceDataPointVO;
import com.niit.collector.vo.DeviceStatsVO;

import java.util.List;

public interface DeviceIngestionService {

    void ingest(DeviceDataPointDTO dto);

    List<DeviceDataPointVO> getDataPoints(String deviceUuid, String from, String to);

    DeviceDataPointVO getLatest(String deviceUuid);

    DeviceStatsVO getStats();

    DeviceStatsVO getStatsByDevices(List<String> deviceUuids);
}
