package com.niit.industrialgasalarmcorporate.domain.operationlog;

import java.time.LocalDateTime;
import java.util.UUID;

public class OperationLog {

    private final String logId;
    private final String operatorUuid;
    private final String operatorName;
    private final String operation;
    private final String targetType;
    private final String targetId;
    private String targetName;
    private String detail;
    private String businessPurpose;
    private final String ip;
    private final LocalDateTime createdAt;

    public OperationLog(String operatorUuid, String operatorName, String operation,
                        String targetType, String targetId, String targetName,
                        String detail, String businessPurpose, String ip) {
        this.logId = UUID.randomUUID().toString();
        this.operatorUuid = operatorUuid;
        this.operatorName = operatorName;
        this.operation = operation;
        this.targetType = targetType;
        this.targetId = targetId;
        this.targetName = targetName;
        this.detail = detail;
        this.businessPurpose = businessPurpose;
        this.ip = ip;
        this.createdAt = LocalDateTime.now();
    }

    public OperationLog(String logId, String operatorUuid, String operatorName,
                        String operation, String targetType, String targetId,
                        String targetName, String detail, String businessPurpose,
                        String ip, LocalDateTime createdAt) {
        this.logId = logId;
        this.operatorUuid = operatorUuid;
        this.operatorName = operatorName;
        this.operation = operation;
        this.targetType = targetType;
        this.targetId = targetId;
        this.targetName = targetName;
        this.detail = detail;
        this.businessPurpose = businessPurpose;
        this.ip = ip;
        this.createdAt = createdAt;
    }

    public String getLogId() { return logId; }
    public String getOperatorUuid() { return operatorUuid; }
    public String getOperatorName() { return operatorName; }
    public String getOperation() { return operation; }
    public String getTargetType() { return targetType; }
    public String getTargetId() { return targetId; }
    public String getTargetName() { return targetName; }
    public String getDetail() { return detail; }
    public String getBusinessPurpose() { return businessPurpose; }
    public String getIp() { return ip; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
