package com.niit.industrialgasalarmcorporate.assembler;

import com.niit.industrialgasalarmcorporate.application.alert.dto.CreateAlertRuleDTO;
import com.niit.industrialgasalarmcorporate.application.alert.dto.UpdateAlertRuleDTO;
import com.niit.industrialgasalarmcorporate.application.alert.vo.AlertRuleVO;
import com.niit.industrialgasalarmcorporate.domain.alert.AlertRule;
import com.niit.industrialgasalarmcorporate.domain.alert.AlertRuleType;
import com.niit.industrialgasalarmcorporate.domain.alert.AlertSeverity;

import java.time.format.DateTimeFormatter;

public final class AlertRuleAssembler {

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private AlertRuleAssembler() {}

    public static AlertRule toEntity(CreateAlertRuleDTO dto) {
        return new AlertRule(
                dto.getName(),
                dto.getDeviceUuid(),
                AlertRuleType.valueOf(dto.getRuleType()),
                dto.getGasType(),
                dto.getThreshold(),
                dto.getDurationSeconds(),
                AlertSeverity.valueOf(dto.getSeverity()),
                dto.isAutoCreateWorkOrder()
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
                dto.isAutoCreateWorkOrder()
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
