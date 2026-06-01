package com.niit.industrialgasalarmcorporate.application.operationlog.service;

import com.niit.industrialgasalarmcorporate.application.operationlog.vo.OperationLogVO;
import com.niit.industrialgasalarmcorporate.common.base.Page;

public interface OperationLogService {

    void record(String operatorUuid, String operatorName, String operation,
                String targetType, String targetId, String targetName,
                String detail, String ip);

    OperationLogVO getById(String logId);

    Page<OperationLogVO> listWithFilter(String operatorName, String operation,
                                         String targetType, int page, int size);
}
