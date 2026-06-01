package com.niit.industrialgasalarmcorporate.assembler;

import com.niit.industrialgasalarmcorporate.application.operationlog.vo.OperationLogVO;
import com.niit.industrialgasalarmcorporate.domain.operationlog.OperationLog;

import java.time.format.DateTimeFormatter;

public final class OperationLogAssembler {

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private OperationLogAssembler() {}

    public static OperationLogVO toVO(OperationLog log) {
        OperationLogVO vo = new OperationLogVO();
        vo.setLogId(log.getLogId());
        vo.setOperatorUuid(log.getOperatorUuid());
        vo.setOperatorName(log.getOperatorName());
        vo.setOperation(log.getOperation());
        vo.setTargetType(log.getTargetType());
        vo.setTargetId(log.getTargetId());
        vo.setTargetName(log.getTargetName());
        vo.setDetail(log.getDetail());
        vo.setIp(log.getIp());
        vo.setCreatedAt(log.getCreatedAt().format(DTF));
        return vo;
    }
}
