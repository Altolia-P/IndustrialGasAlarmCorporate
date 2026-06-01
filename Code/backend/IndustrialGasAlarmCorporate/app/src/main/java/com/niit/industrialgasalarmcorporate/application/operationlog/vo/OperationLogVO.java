package com.niit.industrialgasalarmcorporate.application.operationlog.vo;

import lombok.Data;

@Data
public class OperationLogVO {

    private String logId;
    private String operatorUuid;
    private String operatorName;
    private String operation;
    private String targetType;
    private String targetId;
    private String targetName;
    private String detail;
    private String ip;
    private String createdAt;
}
