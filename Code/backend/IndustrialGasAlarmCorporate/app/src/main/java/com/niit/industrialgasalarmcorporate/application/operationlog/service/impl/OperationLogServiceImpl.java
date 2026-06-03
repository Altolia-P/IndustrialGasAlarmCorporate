package com.niit.industrialgasalarmcorporate.application.operationlog.service.impl;

import com.niit.industrialgasalarmcorporate.application.operationlog.service.OperationLogService;
import com.niit.industrialgasalarmcorporate.application.operationlog.vo.OperationLogVO;
import com.niit.industrialgasalarmcorporate.assembler.OperationLogAssembler;
import com.niit.industrialgasalarmcorporate.common.base.Page;
import com.niit.industrialgasalarmcorporate.domain.operationlog.OperationLog;
import com.niit.industrialgasalarmcorporate.domain.operationlog.OperationLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OperationLogServiceImpl implements OperationLogService {

    private final OperationLogRepository repository;

    @Override
    @Transactional
    public void record(String operatorUuid, String operatorName, String operation,
                       String targetType, String targetId, String targetName,
                       String detail, String businessPurpose, String ip) {
        OperationLog log = new OperationLog(
                operatorUuid, operatorName, operation, targetType,
                targetId, targetName, detail, businessPurpose, ip);
        repository.save(log);
    }

    @Override
    @Transactional(readOnly = true)
    public OperationLogVO getById(String logId) {
        OperationLog log = repository.findById(logId).orElse(null);
        return log != null ? OperationLogAssembler.toVO(log) : null;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OperationLogVO> listWithFilter(String operatorName, String operation,
                                                String targetType, int page, int size) {
        Page<OperationLog> domainPage = repository.findAllWithFilter(
                operatorName, operation, targetType, page, size);
        return new Page<>(
                domainPage.getContent().stream().map(OperationLogAssembler::toVO).collect(Collectors.toList()),
                domainPage.getTotalElements(),
                domainPage.getSize(),
                domainPage.getNumber()
        );
    }
}
