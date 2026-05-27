package com.niit.industrialgasalarmcorporate.domain.alert;

import java.util.List;
import java.util.Optional;

public interface AlertRuleRepository {

    Optional<AlertRule> findById(String ruleUuid);

    List<AlertRule> findByDeviceUuid(String deviceUuid);

    List<AlertRule> findAllEnabled();

    List<AlertRule> findAllGlobal();

    void save(AlertRule rule);

    void deleteById(String ruleUuid);
}
