package com.niit.industrialgasalarmcorporate.interfaces.admin;

import com.niit.industrialgasalarmcorporate.application.alert.dto.CreateAlertRuleDTO;
import com.niit.industrialgasalarmcorporate.application.alert.dto.UpdateAlertRuleDTO;
import com.niit.industrialgasalarmcorporate.application.alert.service.AlertRuleService;
import com.niit.industrialgasalarmcorporate.application.alert.vo.AlertRuleVO;
import com.niit.industrialgasalarmcorporate.common.base.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.niit.industrialgasalarmcorporate.infrastructure.aop.LogOperation;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminAlertRuleController {

    private final AlertRuleService alertRuleService;

    @GetMapping("/alert-rules")
    public Result<List<AlertRuleVO>> getRules() {
        return Result.ok(alertRuleService.findAll());
    }

    @GetMapping("/alert-rules/{uuid}")
    public Result<AlertRuleVO> getRule(@PathVariable String uuid) {
        return Result.ok(alertRuleService.getRule(uuid));
    }

    @LogOperation(operation = "CREATE", targetType = "ALERT_RULE")
    @PostMapping("/alert-rules")
    public Result<AlertRuleVO> createRule(@Valid @RequestBody CreateAlertRuleDTO dto) {
        return Result.ok("创建成功", alertRuleService.createRule(dto));
    }

    @LogOperation(operation = "UPDATE", targetType = "ALERT_RULE")
    @PutMapping("/alert-rules/{uuid}")
    public Result<AlertRuleVO> updateRule(@PathVariable String uuid, @Valid @RequestBody UpdateAlertRuleDTO dto) {
        return Result.ok("更新成功", alertRuleService.updateRule(uuid, dto));
    }

    @LogOperation(operation = "DELETE", targetType = "ALERT_RULE")
    @DeleteMapping("/alert-rules/{uuid}")
    public Result<Void> deleteRule(@PathVariable String uuid) {
        alertRuleService.deleteRule(uuid);
        return Result.ok("删除成功", null);
    }

    @PutMapping("/alert-rules/{uuid}/enable")
    public Result<Void> enableRule(@PathVariable String uuid) {
        alertRuleService.enableRule(uuid);
        return Result.ok("已启用", null);
    }

    @PutMapping("/alert-rules/{uuid}/disable")
    public Result<Void> disableRule(@PathVariable String uuid) {
        alertRuleService.disableRule(uuid);
        return Result.ok("已禁用", null);
    }
}
