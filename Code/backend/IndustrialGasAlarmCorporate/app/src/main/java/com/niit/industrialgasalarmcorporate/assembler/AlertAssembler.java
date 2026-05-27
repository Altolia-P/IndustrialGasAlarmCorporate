package com.niit.industrialgasalarmcorporate.assembler;

import com.niit.industrialgasalarmcorporate.application.alert.vo.AlertVO;
import com.niit.industrialgasalarmcorporate.domain.alert.Alert;

import java.time.format.DateTimeFormatter;

public final class AlertAssembler {

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private AlertAssembler() {}

    public static AlertVO toVO(Alert alert) {
        AlertVO vo = new AlertVO();
        vo.setAlertUuid(alert.getAlertUuid());
        vo.setDeviceUuid(alert.getDeviceUuid());
        vo.setRuleUuid(alert.getRuleUuid());
        vo.setAlertType(alert.getAlertType().name());
        vo.setSeverity(alert.getSeverity().name());
        if (alert.getConcentration() != null) {
            vo.setConcentration(alert.getConcentration().toPlainString());
        }
        if (alert.getThreshold() != null) {
            vo.setThreshold(alert.getThreshold().toPlainString());
        }
        vo.setMessage(alert.getMessage());
        vo.setStatus(alert.getStatus().name());
        vo.setTriggeredAt(alert.getTriggeredAt().format(DTF));
        if (alert.getConfirmedAt() != null) {
            vo.setConfirmedAt(alert.getConfirmedAt().format(DTF));
        }
        vo.setConfirmedBy(alert.getConfirmedBy());
        if (alert.getResolvedAt() != null) {
            vo.setResolvedAt(alert.getResolvedAt().format(DTF));
        }
        vo.setResolvedBy(alert.getResolvedBy());
        vo.setWorkOrderUuid(alert.getWorkOrderUuid());
        vo.setCreatedAt(alert.getCreatedAt().format(DTF));
        return vo;
    }
}
