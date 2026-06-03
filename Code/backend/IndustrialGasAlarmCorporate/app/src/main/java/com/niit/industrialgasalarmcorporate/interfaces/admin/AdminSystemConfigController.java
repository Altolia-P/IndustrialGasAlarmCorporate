package com.niit.industrialgasalarmcorporate.interfaces.admin;

import com.niit.industrialgasalarmcorporate.application.systemconfig.dto.CreateSystemConfigDTO;
import com.niit.industrialgasalarmcorporate.application.systemconfig.dto.UpdateSystemConfigDTO;
import com.niit.industrialgasalarmcorporate.application.systemconfig.service.SystemConfigService;
import com.niit.industrialgasalarmcorporate.application.systemconfig.vo.SystemConfigVO;
import com.niit.industrialgasalarmcorporate.common.base.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.niit.industrialgasalarmcorporate.infrastructure.aop.LogOperation;

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

    @LogOperation(operation = "CREATE", targetType = "SYSTEM_CONFIG")
    @PostMapping("/system-configs")
    public Result<SystemConfigVO> create(@Valid @RequestBody CreateSystemConfigDTO dto) {
        return Result.ok("创建成功", systemConfigService.create(
                dto.getConfigKey(), dto.getConfigValue(), dto.getDescription()));
    }

    @LogOperation(operation = "UPDATE", targetType = "SYSTEM_CONFIG")
    @PutMapping("/system-configs/{configKey}")
    public Result<SystemConfigVO> update(@PathVariable String configKey,
                                         @Valid @RequestBody UpdateSystemConfigDTO dto) {
        return Result.ok("更新成功", systemConfigService.update(configKey, dto.getConfigValue(), dto.getDescription()));
    }

    @LogOperation(operation = "DELETE", targetType = "SYSTEM_CONFIG")
    @DeleteMapping("/system-configs/{configKey}")
    public Result<Void> delete(@PathVariable String configKey) {
        systemConfigService.delete(configKey);
        return Result.ok("删除成功", null);
    }
}
