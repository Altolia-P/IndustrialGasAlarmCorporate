package com.niit.industrialgasalarmcorporate.application.systemconfig.service.impl;

import com.niit.industrialgasalarmcorporate.application.systemconfig.service.SystemConfigService;
import com.niit.industrialgasalarmcorporate.application.systemconfig.vo.SystemConfigVO;
import com.niit.industrialgasalarmcorporate.assembler.SystemConfigAssembler;
import com.niit.industrialgasalarmcorporate.common.enums.ErrorCode;
import com.niit.industrialgasalarmcorporate.common.exception.BusinessException;
import com.niit.industrialgasalarmcorporate.domain.systemconfig.SystemConfig;
import com.niit.industrialgasalarmcorporate.domain.systemconfig.SystemConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SystemConfigServiceImpl implements SystemConfigService {

    private final SystemConfigRepository repository;

    @Override
    @Transactional(readOnly = true)
    public SystemConfigVO getByKey(String configKey) {
        SystemConfig config = repository.findByKey(configKey)
                .orElseThrow(() -> new BusinessException(ErrorCode.SYSTEM_CONFIG_NOT_FOUND));
        return SystemConfigAssembler.toVO(config);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SystemConfigVO> listAll() {
        return repository.findAll().stream()
                .map(SystemConfigAssembler::toVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SystemConfigVO update(String configKey, String configValue, String description) {
        SystemConfig config = repository.findByKey(configKey)
                .orElseThrow(() -> new BusinessException(ErrorCode.SYSTEM_CONFIG_NOT_FOUND));
        config.update(configValue, description);
        repository.save(config);
        return SystemConfigAssembler.toVO(config);
    }
}
