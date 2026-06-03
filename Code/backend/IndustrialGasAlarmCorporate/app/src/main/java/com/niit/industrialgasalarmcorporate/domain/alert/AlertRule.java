package com.niit.industrialgasalarmcorporate.domain.alert;

import com.niit.industrialgasalarmcorporate.common.enums.ErrorCode;
import com.niit.industrialgasalarmcorporate.common.exception.BusinessException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class AlertRule {

    private final String ruleUuid;
    private String name;
    private String deviceUuid;
    private AlertRuleType ruleType;
    private String gasType;
    private BigDecimal threshold;
    private int durationSeconds;
    private AlertSeverity severity;
    private boolean autoCreateWorkOrder;
    private boolean enabled;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AlertRule(String name, String deviceUuid, AlertRuleType ruleType, String gasType,
                     BigDecimal threshold, int durationSeconds, AlertSeverity severity,
                     boolean autoCreateWorkOrder) {
        this.ruleUuid = UUID.randomUUID().toString();
        this.name = name;
        this.deviceUuid = deviceUuid;
        this.ruleType = ruleType;
        this.gasType = gasType;
        this.threshold = threshold;
        this.durationSeconds = durationSeconds;
        this.severity = severity;
        this.autoCreateWorkOrder = autoCreateWorkOrder;
        this.enabled = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public AlertRule(String ruleUuid, String name, String deviceUuid, AlertRuleType ruleType,
                     String gasType, BigDecimal threshold, int durationSeconds,
                     AlertSeverity severity, boolean autoCreateWorkOrder, boolean enabled,
                     LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.ruleUuid = ruleUuid;
        this.name = name;
        this.deviceUuid = deviceUuid;
        this.ruleType = ruleType;
        this.gasType = gasType;
        this.threshold = threshold;
        this.durationSeconds = durationSeconds;
        this.severity = severity;
        this.autoCreateWorkOrder = autoCreateWorkOrder;
        this.enabled = enabled;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public boolean matches(String deviceUuid, String gasType) {
        if (!enabled) {
            return false;
        }
        if (this.deviceUuid != null && !this.deviceUuid.equals(deviceUuid)) {
            return false;
        }
        if (this.gasType != null && !this.gasType.equals(gasType)) {
            return false;
        }
        return true;
    }

    public boolean evaluate(BigDecimal concentration) {
        if (threshold == null) {
            return false;
        }
        return concentration.compareTo(threshold) > 0;
    }

    public void enable() {
        this.enabled = true;
    }

    public void disable() {
        this.enabled = false;
    }

    public void update(String name, AlertRuleType ruleType, String gasType, BigDecimal threshold,
                       int durationSeconds, AlertSeverity severity, Boolean autoCreateWorkOrder) {
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
        if (ruleType != null) {
            this.ruleType = ruleType;
        }
        if (gasType != null) {
            this.gasType = gasType;
        }
        if (threshold != null) {
            this.threshold = threshold;
        }
        if (durationSeconds > 0) {
            this.durationSeconds = durationSeconds;
        }
        if (severity != null) {
            this.severity = severity;
        }
        this.autoCreateWorkOrder = autoCreateWorkOrder;
    }

    public String getRuleUuid() { return ruleUuid; }
    public String getName() { return name; }
    public String getDeviceUuid() { return deviceUuid; }
    public AlertRuleType getRuleType() { return ruleType; }
    public String getGasType() { return gasType; }
    public BigDecimal getThreshold() { return threshold; }
    public int getDurationSeconds() { return durationSeconds; }
    public AlertSeverity getSeverity() { return severity; }
    public boolean isAutoCreateWorkOrder() { return autoCreateWorkOrder; }
    public boolean isEnabled() { return enabled; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
