package com.niit.industrialgasalarmcorporate.application.device.service.impl;

import com.niit.industrialgasalarmcorporate.application.device.dto.DeviceDataPointDTO;
import com.niit.industrialgasalarmcorporate.application.device.service.DeviceIngestionService;
import com.niit.industrialgasalarmcorporate.application.device.vo.DeviceDataPointVO;
import com.niit.industrialgasalarmcorporate.infrastructure.feign.DeviceDataClient;
import com.niit.industrialgasalarmcorporate.infrastructure.feign.dto.DeviceDataPointFeignVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceIngestionServiceImpl implements DeviceIngestionService {

    private final DeviceDataClient deviceDataClient;

    @Override
    public void ingest(DeviceDataPointDTO dto) {
        deviceDataClient.ingestData(toFeignVO(dto));
    }

    @Override
    public List<DeviceDataPointVO> getDataPoints(String deviceUuid, String from, String to) {
        List<DeviceDataPointFeignVO> feignVOs = deviceDataClient.getDataPoints(deviceUuid, from, to);
        if (feignVOs == null) {
            return Collections.emptyList();
        }
        return feignVOs.stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    @Override
    public DeviceDataPointVO getLatest(String deviceUuid) {
        DeviceDataPointFeignVO feignVO = deviceDataClient.getLatest(deviceUuid);
        if (feignVO == null) {
            return null;
        }
        return toVO(feignVO);
    }

    private DeviceDataPointFeignVO toFeignVO(DeviceDataPointDTO dto) {
        DeviceDataPointFeignVO vo = new DeviceDataPointFeignVO();
        vo.setDeviceUuid(dto.getDeviceUuid());
        vo.setTimestamp(dto.getTimestamp() != null ? dto.getTimestamp().toString() : null);
        vo.setConcentration(dto.getConcentration() != null ? dto.getConcentration().toPlainString() : null);
        vo.setBattery(dto.getBattery() != null ? dto.getBattery().toPlainString() : null);
        vo.setTemperature(dto.getTemperature() != null ? dto.getTemperature().toPlainString() : null);
        vo.setHumidity(dto.getHumidity() != null ? dto.getHumidity().toPlainString() : null);
        vo.setSignalStrength(dto.getSignalStrength());
        return vo;
    }

    private DeviceDataPointVO toVO(DeviceDataPointFeignVO feignVO) {
        DeviceDataPointVO vo = new DeviceDataPointVO();
        vo.setDeviceUuid(feignVO.getDeviceUuid());
        vo.setTimestamp(feignVO.getTimestamp());
        vo.setConcentration(feignVO.getConcentration());
        vo.setBattery(feignVO.getBattery());
        vo.setTemperature(feignVO.getTemperature());
        vo.setHumidity(feignVO.getHumidity());
        vo.setSignalStrength(feignVO.getSignalStrength());
        vo.setCreatedAt(feignVO.getCreatedAt());
        return vo;
    }
}
