package com.niit.industrialgasalarmcorporate.domain.operationlog;

import com.niit.industrialgasalarmcorporate.common.base.Page;

import java.util.Optional;

public interface OperationLogRepository {

    void save(OperationLog log);

    Optional<OperationLog> findById(String logId);

    Page<OperationLog> findAllWithFilter(String operatorName, String operation,
                                          String targetType, int page, int size);
}
