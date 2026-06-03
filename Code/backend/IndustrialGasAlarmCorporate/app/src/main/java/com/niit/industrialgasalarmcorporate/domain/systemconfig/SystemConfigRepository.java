package com.niit.industrialgasalarmcorporate.domain.systemconfig;

import java.util.List;
import java.util.Optional;

public interface SystemConfigRepository {

    Optional<SystemConfig> findByKey(String configKey);

    List<SystemConfig> findAll();

    void save(SystemConfig config);

    void deleteByKey(String configKey);
}
