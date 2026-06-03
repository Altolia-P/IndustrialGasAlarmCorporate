package com.niit.industrialgasalarmcorporate.interfaces.pub;

import com.niit.industrialgasalarmcorporate.common.base.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/public")
public class HealthController {

    @GetMapping("/health")
    public Result<Map<String, Object>> health() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("status", "UP");
        return Result.ok(info);
    }
}
