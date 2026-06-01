package com.niit.industrialgasalarmcorporate.infrastructure.repository.impl;

import com.niit.industrialgasalarmcorporate.domain.systemconfig.SystemConfig;
import com.niit.industrialgasalarmcorporate.domain.systemconfig.SystemConfigRepository;
import com.niit.industrialgasalarmcorporate.infrastructure.repository.mapper.SystemConfigMapper;
import com.niit.industrialgasalarmcorporate.infrastructure.repository.po.SystemConfigPO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class SystemConfigRepositoryImpl implements SystemConfigRepository {

    private final SystemConfigMapper mapper;

    @Override
    public Optional<SystemConfig> findByKey(String configKey) {
        SystemConfigPO po = mapper.selectById(configKey);
        if (po == null) {
            return Optional.empty();
        }
        return Optional.of(toDomain(po));
    }

    @Override
    public List<SystemConfig> findAll() {
        return mapper.selectList(null).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void save(SystemConfig config) {
        SystemConfigPO po = toPO(config);
        if (mapper.selectById(config.getConfigKey()) != null) {
            mapper.updateById(po);
        } else {
            mapper.insert(po);
        }
    }

    private SystemConfig toDomain(SystemConfigPO po) {
        return new SystemConfig(
                po.getConfigKey(),
                po.getConfigValue(),
                po.getDescription(),
                po.getVersion(),
                po.getUpdatedAt()
        );
    }

    private SystemConfigPO toPO(SystemConfig config) {
        SystemConfigPO po = new SystemConfigPO();
        po.setConfigKey(config.getConfigKey());
        po.setConfigValue(config.getConfigValue());
        po.setDescription(config.getDescription());
        return po;
    }
}
