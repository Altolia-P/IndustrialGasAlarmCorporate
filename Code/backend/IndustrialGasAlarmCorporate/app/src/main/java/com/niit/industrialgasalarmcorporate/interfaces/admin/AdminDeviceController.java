package com.niit.industrialgasalarmcorporate.interfaces.admin;

import com.niit.industrialgasalarmcorporate.application.device.dto.CreateDeviceDTO;
import com.niit.industrialgasalarmcorporate.application.device.dto.UpdateDeviceDTO;
import com.niit.industrialgasalarmcorporate.application.device.service.DeviceIngestionService;
import com.niit.industrialgasalarmcorporate.application.device.service.DeviceService;
import com.niit.industrialgasalarmcorporate.application.device.vo.DeviceDataPointVO;
import com.niit.industrialgasalarmcorporate.application.device.vo.DeviceVO;
import com.niit.industrialgasalarmcorporate.common.base.Page;
import com.niit.industrialgasalarmcorporate.common.base.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminDeviceController {

    private final DeviceService deviceService;
    private final DeviceIngestionService deviceIngestionService;

    @GetMapping("/devices")
    public Result<Page<DeviceVO>> getDevices(
            @RequestParam(required = false) String customerUuid,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) String gasType,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return Result.ok(deviceService.findDevices(customerUuid, model, gasType, status, page, size));
    }

    @GetMapping("/devices/{uuid}")
    public Result<DeviceVO> getDevice(@PathVariable String uuid) {
        return Result.ok(deviceService.getDevice(uuid));
    }

    @PostMapping("/devices")
    public Result<DeviceVO> createDevice(@Valid @RequestBody CreateDeviceDTO dto) {
        return Result.ok("创建成功", deviceService.createDevice(dto));
    }

    @PutMapping("/devices/{uuid}")
    public Result<DeviceVO> updateDevice(@PathVariable String uuid, @Valid @RequestBody UpdateDeviceDTO dto) {
        return Result.ok("更新成功", deviceService.updateDevice(uuid, dto));
    }

    @DeleteMapping("/devices/{uuid}")
    public Result<Void> deleteDevice(@PathVariable String uuid) {
        deviceService.deleteDevice(uuid);
        return Result.ok("删除成功", null);
    }

    @PostMapping("/devices/{uuid}/mark-abnormal")
    public Result<Void> markAbnormal(@PathVariable String uuid) {
        deviceService.markAbnormal(uuid);
        return Result.ok("已标记为异常", null);
    }

    @PostMapping("/devices/{uuid}/mark-normal")
    public Result<Void> markNormal(@PathVariable String uuid) {
        deviceService.markNormal(uuid);
        return Result.ok("已恢复为正常", null);
    }

    @PostMapping("/devices/{uuid}/mark-offline")
    public Result<Void> markOffline(@PathVariable String uuid) {
        deviceService.markOffline(uuid);
        return Result.ok("已标记为离线", null);
    }

    @PostMapping("/devices/{uuid}/start-maintenance")
    public Result<Void> startMaintenance(@PathVariable String uuid) {
        deviceService.startMaintenance(uuid);
        return Result.ok("已进入维护模式", null);
    }

    @PostMapping("/devices/{uuid}/end-maintenance")
    public Result<Void> endMaintenance(@PathVariable String uuid) {
        deviceService.endMaintenance(uuid);
        return Result.ok("已结束维护", null);
    }

    @GetMapping("/devices/{uuid}/data")
    public Result<List<DeviceDataPointVO>> getDataPoints(
            @PathVariable String uuid,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        return Result.ok(deviceIngestionService.getDataPoints(uuid, from, to));
    }

    @GetMapping("/devices/{uuid}/latest")
    public Result<DeviceDataPointVO> getLatest(@PathVariable String uuid) {
        return Result.ok(deviceIngestionService.getLatest(uuid));
    }
}
