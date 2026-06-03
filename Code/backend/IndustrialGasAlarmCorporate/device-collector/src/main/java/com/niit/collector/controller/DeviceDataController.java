package com.niit.collector.controller;

import com.niit.collector.dto.DeviceDataPointDTO;
import com.niit.collector.service.DeviceIngestionService;
import com.niit.collector.vo.DeviceDataPointVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/collect")
@RequiredArgsConstructor
public class DeviceDataController {

    private final DeviceIngestionService deviceIngestionService;

    @PostMapping("/device-data")
    public Map<String, Object> ingestData(@Valid @RequestBody DeviceDataPointDTO dto) {
        log.info("采集器接收数据: deviceUuid={}, concentration={}", dto.getDeviceUuid(), dto.getConcentration());
        deviceIngestionService.ingest(dto);
        return Map.of("code", 0, "message", "success");
    }

    @GetMapping("/device-data")
    public List<DeviceDataPointVO> getDataPoints(@RequestParam String deviceUuid,
                                                  @RequestParam(required = false) String from,
                                                  @RequestParam(required = false) String to) {
        return deviceIngestionService.getDataPoints(deviceUuid, from, to);
    }

    @GetMapping("/device-data/latest")
    public DeviceDataPointVO getLatest(@RequestParam String deviceUuid) {
        return deviceIngestionService.getLatest(deviceUuid);
    }

    @GetMapping("/device-data/stats")
    public com.niit.collector.vo.DeviceStatsVO getStats() {
        return deviceIngestionService.getStats();
    }

    @GetMapping("/device-data/stats/scoped")
    public com.niit.collector.vo.DeviceStatsVO getStatsByDevices(@RequestParam List<String> deviceUuids) {
        return deviceIngestionService.getStatsByDevices(deviceUuids);
    }
}
