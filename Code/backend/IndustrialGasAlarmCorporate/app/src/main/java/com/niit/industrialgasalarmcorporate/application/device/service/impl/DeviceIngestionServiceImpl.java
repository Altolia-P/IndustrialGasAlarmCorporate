package com.niit.industrialgasalarmcorporate.application.device.service.impl;

import com.niit.industrialgasalarmcorporate.application.device.dto.DeviceDataPointDTO;
import com.niit.industrialgasalarmcorporate.application.device.service.DeviceIngestionService;
import com.niit.industrialgasalarmcorporate.application.device.vo.DeviceDataPointVO;
import com.niit.industrialgasalarmcorporate.assembler.DeviceDataPointAssembler;
import com.niit.industrialgasalarmcorporate.application.alert.service.AlertEngineService;
import com.niit.industrialgasalarmcorporate.common.enums.ErrorCode;
import com.niit.industrialgasalarmcorporate.common.exception.BusinessException;
import com.niit.industrialgasalarmcorporate.domain.device.DeviceDataPoint;
import com.niit.industrialgasalarmcorporate.domain.device.DeviceDataPointRepository;
import com.niit.industrialgasalarmcorporate.domain.device.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeviceIngestionServiceImpl implements DeviceIngestionService {

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final DeviceRepository deviceRepository;
    private final DeviceDataPointRepository deviceDataPointRepository;
    private final AlertEngineService alertEngineService;

    @Override
    @Transactional
    public void ingest(DeviceDataPointDTO dto) {
        if (deviceRepository.findById(dto.getDeviceUuid()).isEmpty()) {
            throw new BusinessException(ErrorCode.DEVICE_NOT_FOUND);
        }
        DeviceDataPoint dataPoint = DeviceDataPointAssembler.toEntity(dto);
        deviceDataPointRepository.save(dataPoint);
        alertEngineService.evaluate(dataPoint);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeviceDataPointVO> getDataPoints(String deviceUuid, String from, String to) {
        LocalDateTime fromTime = from != null ? LocalDateTime.parse(from, DTF) : null;
        LocalDateTime toTime = to != null ? LocalDateTime.parse(to, DTF) : null;
        return deviceDataPointRepository.findByDeviceUuid(deviceUuid, fromTime, toTime).stream()
                .map(DeviceDataPointAssembler::toVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DeviceDataPointVO getLatest(String deviceUuid) {
        return deviceDataPointRepository.findLatest(deviceUuid)
                .map(DeviceDataPointAssembler::toVO)
                .orElseThrow(() -> new BusinessException(ErrorCode.DEVICE_NOT_FOUND));
    }
}
