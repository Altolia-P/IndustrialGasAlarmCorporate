package com.niit.industrialgasalarmcorporate.infrastructure.feign;

import com.niit.industrialgasalarmcorporate.infrastructure.feign.dto.DeviceDataPointFeignVO;
import com.niit.industrialgasalarmcorporate.infrastructure.feign.dto.DeviceStatsFeignVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "device-collector", path = "/api/v1/collect")
public interface DeviceDataClient {

    @PostMapping("/device-data")
    Map<String, Object> ingestData(@RequestBody DeviceDataPointFeignVO dto);

    @GetMapping("/device-data")
    List<DeviceDataPointFeignVO> getDataPoints(@RequestParam String deviceUuid,
                                                @RequestParam(required = false) String from,
                                                @RequestParam(required = false) String to);

    @GetMapping("/device-data/latest")
    DeviceDataPointFeignVO getLatest(@RequestParam String deviceUuid);

    @GetMapping("/device-data/stats")
    DeviceStatsFeignVO getStats();

    @GetMapping("/device-data/stats/scoped")
    DeviceStatsFeignVO getStatsByDevices(@RequestParam List<String> deviceUuids);
}
