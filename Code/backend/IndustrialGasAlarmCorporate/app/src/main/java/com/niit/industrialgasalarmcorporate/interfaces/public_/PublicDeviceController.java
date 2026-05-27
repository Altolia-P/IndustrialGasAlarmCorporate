package com.niit.industrialgasalarmcorporate.interfaces.public_;

import com.niit.industrialgasalarmcorporate.application.device.dto.DeviceDataPointDTO;
import com.niit.industrialgasalarmcorporate.application.device.service.DeviceIngestionService;
import com.niit.industrialgasalarmcorporate.application.device.vo.DeviceDataPointVO;
import com.niit.industrialgasalarmcorporate.common.base.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
public class PublicDeviceController {

    private final DeviceIngestionService deviceIngestionService;

    @PostMapping("/device-data")
    public Result<Void> ingestData(@Valid @RequestBody DeviceDataPointDTO dto) {
        deviceIngestionService.ingest(dto);
        return Result.ok("数据上报成功", null);
    }
}
