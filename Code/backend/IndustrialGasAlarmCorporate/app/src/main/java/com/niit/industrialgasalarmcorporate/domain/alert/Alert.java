package com.niit.industrialgasalarmcorporate.domain.alert;

import com.niit.industrialgasalarmcorporate.common.enums.ErrorCode;
import com.niit.industrialgasalarmcorporate.common.exception.BusinessException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Alert {

    private final String alertUuid;
    private final String deviceUuid;
    private String ruleUuid;
    private AlertRuleType alertType;
    private AlertSeverity severity;
    private BigDecimal concentration;
    private BigDecimal threshold;
    private String message;
    private AlertStatus status;
    private final LocalDateTime triggeredAt;
    private LocalDateTime confirmedAt;
    private String confirmedBy;
    private LocalDateTime resolvedAt;
    private String resolvedBy;
    private String workOrderUuid;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer version;

    public Alert(String deviceUuid, String ruleUuid, AlertRuleType alertType, AlertSeverity severity,
                 BigDecimal concentration, BigDecimal threshold, String message) {
        this.alertUuid = UUID.randomUUID().toString();
        this.deviceUuid = deviceUuid;
        this.ruleUuid = ruleUuid;
        this.alertType = alertType;
        this.severity = severity;
        this.concentration = concentration;
        this.threshold = threshold;
        this.message = message;
        this.status = AlertStatus.PENDING;
        this.triggeredAt = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public Alert(String alertUuid, String deviceUuid, String ruleUuid, AlertRuleType alertType,
                 AlertSeverity severity, BigDecimal concentration, BigDecimal threshold,
                 String message, AlertStatus status, LocalDateTime triggeredAt,
                 LocalDateTime confirmedAt, String confirmedBy, LocalDateTime resolvedAt,
                 String resolvedBy, String workOrderUuid, LocalDateTime createdAt,
                 LocalDateTime updatedAt, Integer version) {
        this.alertUuid = alertUuid;
        this.deviceUuid = deviceUuid;
        this.ruleUuid = ruleUuid;
        this.alertType = alertType;
        this.severity = severity;
        this.concentration = concentration;
        this.threshold = threshold;
        this.message = message;
        this.status = status;
        this.triggeredAt = triggeredAt;
        this.confirmedAt = confirmedAt;
        this.confirmedBy = confirmedBy;
        this.resolvedAt = resolvedAt;
        this.resolvedBy = resolvedBy;
        this.workOrderUuid = workOrderUuid;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    public void confirm(String confirmedBy) {
        if (this.status != AlertStatus.PENDING) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "仅待处理状态的报警可确认");
        }
        this.status = AlertStatus.CONFIRMED;
        this.confirmedBy = confirmedBy;
        this.confirmedAt = LocalDateTime.now();
    }

    public void resolve(String resolvedBy) {
        if (this.status != AlertStatus.CONFIRMED) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "仅已确认状态的报警可解决");
        }
        this.status = AlertStatus.RESOLVED;
        this.resolvedBy = resolvedBy;
        this.resolvedAt = LocalDateTime.now();
    }

    public void close() {
        if (this.status == AlertStatus.RESOLVED || this.status == AlertStatus.CONFIRMED
                || this.status == AlertStatus.PENDING) {
            this.status = AlertStatus.CLOSED;
            return;
        }
        throw new BusinessException(ErrorCode.VALIDATION_ERROR, "仅待处理、已确认或已解决状态的报警可关闭");
    }

    public String getAlertUuid() { return alertUuid; }
    public String getDeviceUuid() { return deviceUuid; }
    public String getRuleUuid() { return ruleUuid; }
    public AlertRuleType getAlertType() { return alertType; }
    public AlertSeverity getSeverity() { return severity; }
    public BigDecimal getConcentration() { return concentration; }
    public BigDecimal getThreshold() { return threshold; }
    public String getMessage() { return message; }
    public AlertStatus getStatus() { return status; }
    public LocalDateTime getTriggeredAt() { return triggeredAt; }
    public LocalDateTime getConfirmedAt() { return confirmedAt; }
    public String getConfirmedBy() { return confirmedBy; }
    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public String getResolvedBy() { return resolvedBy; }
    public String getWorkOrderUuid() { return workOrderUuid; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public Integer getVersion() { return version; }

    public void setWorkOrderUuid(String workOrderUuid) { this.workOrderUuid = workOrderUuid; }
}
