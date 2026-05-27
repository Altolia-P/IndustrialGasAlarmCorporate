package com.niit.simulator.controller;

import com.niit.simulator.config.SimulatorProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/simulator")
@RequiredArgsConstructor
public class SimulatorController {

    private final SimulatorProperties properties;

    @GetMapping("/status")
    public Map<String, Object> status() {
        return Map.of(
                "enabled", properties.isEnabled(),
                "intervalMs", properties.getIntervalMs(),
                "deviceCount", properties.getDevices().size(),
                "targetUrl", properties.getTargetUrl()
        );
    }

    @PostMapping("/start")
    public Map<String, Object> start() {
        properties.setEnabled(true);
        return Map.of("enabled", true, "message", "模拟器已启动");
    }

    @PostMapping("/stop")
    public Map<String, Object> stop() {
        properties.setEnabled(false);
        return Map.of("enabled", false, "message", "模拟器已停止");
    }
}
