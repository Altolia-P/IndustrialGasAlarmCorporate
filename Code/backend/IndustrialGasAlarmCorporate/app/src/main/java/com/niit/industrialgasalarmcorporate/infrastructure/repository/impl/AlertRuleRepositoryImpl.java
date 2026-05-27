package com.niit.industrialgasalarmcorporate.infrastructure.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.niit.industrialgasalarmcorporate.domain.alert.AlertRule;
import com.niit.industrialgasalarmcorporate.domain.alert.AlertRuleRepository;
import com.niit.industrialgasalarmcorporate.domain.alert.AlertRuleType;
import com.niit.industrialgasalarmcorporate.domain.alert.AlertSeverity;
import com.niit.industrialgasalarmcorporate.infrastructure.repository.mapper.AlertRuleMapper;
import com.niit.industrialgasalarmcorporate.infrastructure.repository.po.AlertRulePO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class AlertRuleRepositoryImpl implements AlertRuleRepository {

    private final AlertRuleMapper alertRuleMapper;

    @Override
    public Optional<AlertRule> findById(String ruleUuid) {
        AlertRulePO po = alertRuleMapper.selectById(ruleUuid);
        if (po == null) {
            return Optional.empty();
        }
        return Optional.of(toDomain(po));
    }

    @Override
    public List<AlertRule> findByDeviceUuid(String deviceUuid) {
        LambdaQueryWrapper<AlertRulePO> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(AlertRulePO::getDeviceUuid, deviceUuid)
                        .or().isNull(AlertRulePO::getDeviceUuid))
                .eq(AlertRulePO::getEnabled, 1);
        return alertRuleMapper.selectList(wrapper).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<AlertRule> findAllEnabled() {
        LambdaQueryWrapper<AlertRulePO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AlertRulePO::getEnabled, 1);
        return alertRuleMapper.selectList(wrapper).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<AlertRule> findAllGlobal() {
        LambdaQueryWrapper<AlertRulePO> wrapper = new LambdaQueryWrapper<>();
        wrapper.isNull(AlertRulePO::getDeviceUuid)
                .eq(AlertRulePO::getEnabled, 1);
        return alertRuleMapper.selectList(wrapper).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void save(AlertRule rule) {
        AlertRulePO po = toPO(rule);
        AlertRulePO existing = alertRuleMapper.selectById(rule.getRuleUuid());
        if (existing != null) {
            alertRuleMapper.updateById(po);
        } else {
            alertRuleMapper.insert(po);
        }
    }

    @Override
    public void deleteById(String ruleUuid) {
        alertRuleMapper.deleteById(ruleUuid);
    }

    private AlertRule toDomain(AlertRulePO po) {
        return new AlertRule(
                po.getRuleUuid(),
                po.getName(),
                po.getDeviceUuid(),
                AlertRuleType.valueOf(po.getRuleType()),
                po.getGasType(),
                po.getThreshold(),
                po.getDurationSeconds(),
                AlertSeverity.valueOf(po.getSeverity()),
                po.getAutoCreateWorkOrder() != null && po.getAutoCreateWorkOrder() == 1,
                po.getEnabled() != null && po.getEnabled() == 1,
                po.getCreatedAt(),
                po.getUpdatedAt()
        );
    }

    private AlertRulePO toPO(AlertRule rule) {
        AlertRulePO po = new AlertRulePO();
        po.setRuleUuid(rule.getRuleUuid());
        po.setName(rule.getName());
        po.setDeviceUuid(rule.getDeviceUuid());
        po.setRuleType(rule.getRuleType().name());
        po.setGasType(rule.getGasType());
        po.setThreshold(rule.getThreshold());
        po.setDurationSeconds(rule.getDurationSeconds());
        po.setSeverity(rule.getSeverity().name());
        po.setAutoCreateWorkOrder(rule.isAutoCreateWorkOrder() ? 1 : 0);
        po.setEnabled(rule.isEnabled() ? 1 : 0);
        return po;
    }
}
