package com.niit.industrialgasalarmcorporate.interfaces.pub;

import com.niit.industrialgasalarmcorporate.application.device.dto.DeviceDataPointDTO;
import com.niit.industrialgasalarmcorporate.application.device.service.DeviceIngestionService;
import com.niit.industrialgasalarmcorporate.application.device.vo.DeviceDataPointVO;
import com.niit.industrialgasalarmcorporate.common.base.Result;
import com.niit.industrialgasalarmcorporate.common.enums.ErrorCode;
import com.niit.industrialgasalarmcorporate.common.exception.BusinessException;
import com.niit.industrialgasalarmcorporate.infrastructure.redis.DeviceDataRateLimitRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
public class PublicDeviceController {

    private final DeviceIngestionService deviceIngestionService;
    private final DeviceDataRateLimitRepository deviceDataRateLimitRepository;

    @PostMapping("/device-data")
    public Result<Void> ingestData(@Valid @RequestBody DeviceDataPointDTO dto) {
        if (!deviceDataRateLimitRepository.tryAcquire(dto.getDeviceUuid())) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "数据上报过于频繁");
        }
        deviceIngestionService.ingest(dto);
        return Result.ok("数据上报成功", null);
    }
}
