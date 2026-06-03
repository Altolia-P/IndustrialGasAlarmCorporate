package com.niit.industrialgasalarmcorporate.assembler;

import com.niit.industrialgasalarmcorporate.application.alert.dto.CreateAlertRuleDTO;
import com.niit.industrialgasalarmcorporate.application.alert.dto.UpdateAlertRuleDTO;
import com.niit.industrialgasalarmcorporate.application.alert.vo.AlertRuleVO;
import com.niit.industrialgasalarmcorporate.common.enums.ErrorCode;
import com.niit.industrialgasalarmcorporate.common.exception.BusinessException;
import com.niit.industrialgasalarmcorporate.domain.alert.AlertRule;
import com.niit.industrialgasalarmcorporate.domain.alert.AlertRuleType;
import com.niit.industrialgasalarmcorporate.domain.alert.AlertSeverity;

import java.time.format.DateTimeFormatter;

public final class AlertRuleAssembler {

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private AlertRuleAssembler() {}

    public static AlertRule toEntity(CreateAlertRuleDTO dto) {
        AlertRuleType ruleType;
        try {
            ruleType = AlertRuleType.valueOf(dto.getRuleType());
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "无效的规则类型: " + dto.getRuleType());
        }
        AlertSeverity severity;
        try {
            severity = AlertSeverity.valueOf(dto.getSeverity());
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "无效的严重级别: " + dto.getSeverity());
        }
        return new AlertRule(
                dto.getName(),
                dto.getDeviceUuid(),
                ruleType,
                dto.getGasType(),
                dto.getThreshold(),
                dto.getDurationSeconds(),
                severity,
                dto.getAutoCreateWorkOrder() != null ? dto.getAutoCreateWorkOrder() : false
        );
    }

    public static void updateEntity(AlertRule rule, UpdateAlertRuleDTO dto) {
        rule.update(
                dto.getName(),
                dto.getRuleType() != null ? AlertRuleType.valueOf(dto.getRuleType()) : null,
                dto.getGasType(),
                dto.getThreshold(),
                dto.getDurationSeconds(),
                dto.getSeverity() != null ? AlertSeverity.valueOf(dto.getSeverity()) : null,
                dto.getAutoCreateWorkOrder()
        );
    }

    public static AlertRuleVO toVO(AlertRule rule) {
        AlertRuleVO vo = new AlertRuleVO();
        vo.setRuleUuid(rule.getRuleUuid());
        vo.setName(rule.getName());
        vo.setDeviceUuid(rule.getDeviceUuid());
        vo.setRuleType(rule.getRuleType().name());
        vo.setGasType(rule.getGasType());
        if (rule.getThreshold() != null) {
            vo.setThreshold(rule.getThreshold().toPlainString());
        }
        vo.setDurationSeconds(rule.getDurationSeconds());
        vo.setSeverity(rule.getSeverity().name());
        vo.setAutoCreateWorkOrder(rule.isAutoCreateWorkOrder());
        vo.setEnabled(rule.isEnabled());
        vo.setCreatedAt(rule.getCreatedAt().format(DTF));
        return vo;
    }
}
