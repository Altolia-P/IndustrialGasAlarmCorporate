package com.niit.industrialgasalarmcorporate.interfaces.pub;

import com.niit.industrialgasalarmcorporate.application.systemconfig.service.SystemConfigService;
import com.niit.industrialgasalarmcorporate.application.systemconfig.vo.SystemConfigVO;
import com.niit.industrialgasalarmcorporate.common.base.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
public class SystemConfigController {

    private final SystemConfigService systemConfigService;

    @GetMapping("/system-configs")
    public Result<List<SystemConfigVO>> listAll() {
        return Result.ok(systemConfigService.listAll());
    }
}
