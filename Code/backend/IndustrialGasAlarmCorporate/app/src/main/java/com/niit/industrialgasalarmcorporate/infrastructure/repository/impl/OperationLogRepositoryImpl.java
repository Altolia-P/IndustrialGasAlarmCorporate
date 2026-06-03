package com.niit.industrialgasalarmcorporate.infrastructure.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.niit.industrialgasalarmcorporate.common.base.Page;
import com.niit.industrialgasalarmcorporate.domain.operationlog.OperationLog;
import com.niit.industrialgasalarmcorporate.domain.operationlog.OperationLogRepository;
import com.niit.industrialgasalarmcorporate.infrastructure.repository.mapper.OperationLogMapper;
import com.niit.industrialgasalarmcorporate.infrastructure.repository.po.OperationLogPO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OperationLogRepositoryImpl implements OperationLogRepository {

    private final OperationLogMapper mapper;

    @Override
    public void save(OperationLog log) {
        mapper.insert(toPO(log));
    }

    @Override
    public Optional<OperationLog> findById(String logId) {
        OperationLogPO po = mapper.selectById(logId);
        if (po == null) {
            return Optional.empty();
        }
        return Optional.of(toDomain(po));
    }

    @Override
    public Page<OperationLog> findAllWithFilter(String operatorName, String operation,
                                                 String targetType, int page, int size) {
        LambdaQueryWrapper<OperationLogPO> wrapper = new LambdaQueryWrapper<>();
        if (operatorName != null && !operatorName.isBlank()) {
            wrapper.like(OperationLogPO::getOperatorName, operatorName);
        }
        if (operation != null && !operation.isBlank()) {
            wrapper.eq(OperationLogPO::getOperation, operation);
        }
        if (targetType != null && !targetType.isBlank()) {
            wrapper.eq(OperationLogPO::getTargetType, targetType);
        }
        wrapper.orderByDesc(OperationLogPO::getCreatedAt);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<OperationLogPO> mpPage =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<OperationLogPO> result =
                mapper.selectPage(mpPage, wrapper);
        List<OperationLog> logs = result.getRecords().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
        return new Page<>(logs, result.getTotal(), (int) result.getSize(), (int) result.getCurrent());
    }

    private OperationLog toDomain(OperationLogPO po) {
        return new OperationLog(
                po.getLogId(), po.getOperatorUuid(), po.getOperatorName(),
                po.getOperation(), po.getTargetType(), po.getTargetId(),
                po.getTargetName(), po.getDetail(), po.getBusinessPurpose(), po.getIp(), po.getCreatedAt()
        );
    }

    private OperationLogPO toPO(OperationLog log) {
        OperationLogPO po = new OperationLogPO();
        po.setLogId(log.getLogId());
        po.setOperatorUuid(log.getOperatorUuid());
        po.setOperatorName(log.getOperatorName());
        po.setOperation(log.getOperation());
        po.setTargetType(log.getTargetType());
        po.setTargetId(log.getTargetId());
        po.setTargetName(log.getTargetName());
        po.setDetail(log.getDetail());
        po.setBusinessPurpose(log.getBusinessPurpose());
        po.setIp(log.getIp());
        return po;
    }
}
