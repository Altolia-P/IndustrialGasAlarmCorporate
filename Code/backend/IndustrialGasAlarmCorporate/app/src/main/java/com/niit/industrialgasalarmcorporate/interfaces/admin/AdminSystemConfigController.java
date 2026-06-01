package com.niit.industrialgasalarmcorporate.interfaces.admin;

import com.niit.industrialgasalarmcorporate.application.systemconfig.dto.UpdateSystemConfigDTO;
import com.niit.industrialgasalarmcorporate.application.systemconfig.service.SystemConfigService;
import com.niit.industrialgasalarmcorporate.application.systemconfig.vo.SystemConfigVO;
import com.niit.industrialgasalarmcorporate.common.base.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminSystemConfigController {

    private final SystemConfigService systemConfigService;

    @GetMapping("/system-configs")
    public Result<List<SystemConfigVO>> listAll() {
        return Result.ok(systemConfigService.listAll());
    }

    @GetMapping("/system-configs/{configKey}")
    public Result<SystemConfigVO> getByKey(@PathVariable String configKey) {
        return Result.ok(systemConfigService.getByKey(configKey));
    }

    @PutMapping("/system-configs/{configKey}")
    public Result<SystemConfigVO> update(@PathVariable String configKey,
                                         @Valid @RequestBody UpdateSystemConfigDTO dto) {
        return Result.ok("更新成功", systemConfigService.update(configKey, dto.getConfigValue(), dto.getDescription()));
    }
}
