package com.niit.industrialgasalarmcorporate.interfaces.admin;

import com.niit.industrialgasalarmcorporate.application.alert.service.AlertService;
import com.niit.industrialgasalarmcorporate.application.alert.vo.AlertVO;
import com.niit.industrialgasalarmcorporate.common.base.Page;
import com.niit.industrialgasalarmcorporate.common.base.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminAlertController {

    private final AlertService alertService;

    @GetMapping("/alerts")
    public Result<Page<AlertVO>> getAlerts(
            @RequestParam(required = false) String deviceUuid,
            @RequestParam(required = false) String alertType,
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return Result.ok(alertService.findAlerts(deviceUuid, alertType, severity, status, page, size));
    }

    @GetMapping("/alerts/{uuid}")
    public Result<AlertVO> getAlert(@PathVariable String uuid) {
        return Result.ok(alertService.getAlert(uuid));
    }

    @PostMapping("/alerts/{uuid}/confirm")
    public Result<Void> confirmAlert(@PathVariable String uuid,
                                     @RequestParam String confirmedBy) {
        alertService.confirmAlert(uuid, confirmedBy);
        return Result.ok("报警已确认", null);
    }

    @PostMapping("/alerts/{uuid}/resolve")
    public Result<Void> resolveAlert(@PathVariable String uuid,
                                     @RequestParam String resolvedBy) {
        alertService.resolveAlert(uuid, resolvedBy);
        return Result.ok("报警已解决", null);
    }

    @PostMapping("/alerts/{uuid}/close")
    public Result<Void> closeAlert(@PathVariable String uuid) {
        alertService.closeAlert(uuid);
        return Result.ok("报警已关闭", null);
    }
}
