package com.niit.industrialgasalarmcorporate.application.alert.service.impl;

import com.niit.industrialgasalarmcorporate.application.alert.dto.CreateAlertRuleDTO;
import com.niit.industrialgasalarmcorporate.application.alert.dto.UpdateAlertRuleDTO;
import com.niit.industrialgasalarmcorporate.application.alert.service.AlertRuleService;
import com.niit.industrialgasalarmcorporate.application.alert.vo.AlertRuleVO;
import com.niit.industrialgasalarmcorporate.assembler.AlertRuleAssembler;
import com.niit.industrialgasalarmcorporate.common.enums.ErrorCode;
import com.niit.industrialgasalarmcorporate.common.exception.BusinessException;
import com.niit.industrialgasalarmcorporate.domain.alert.AlertRule;
import com.niit.industrialgasalarmcorporate.domain.alert.AlertRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlertRuleServiceImpl implements AlertRuleService {

    private final AlertRuleRepository alertRuleRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AlertRuleVO> findAll() {
        return alertRuleRepository.findAllEnabled().stream()
                .map(AlertRuleAssembler::toVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AlertRuleVO getRule(String ruleUuid) {
        AlertRule rule = alertRuleRepository.findById(ruleUuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.ALERT_RULE_NOT_FOUND));
        return AlertRuleAssembler.toVO(rule);
    }

    @Override
    @Transactional
    public AlertRuleVO createRule(CreateAlertRuleDTO dto) {
        AlertRule rule = AlertRuleAssembler.toEntity(dto);
        alertRuleRepository.save(rule);
        return AlertRuleAssembler.toVO(rule);
    }

    @Override
    @Transactional
    public AlertRuleVO updateRule(String ruleUuid, UpdateAlertRuleDTO dto) {
        AlertRule rule = alertRuleRepository.findById(ruleUuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.ALERT_RULE_NOT_FOUND));
        AlertRuleAssembler.updateEntity(rule, dto);
        alertRuleRepository.save(rule);
        return AlertRuleAssembler.toVO(rule);
    }

    @Override
    @Transactional
    public void deleteRule(String ruleUuid) {
        if (alertRuleRepository.findById(ruleUuid).isEmpty()) {
            throw new BusinessException(ErrorCode.ALERT_RULE_NOT_FOUND);
        }
        alertRuleRepository.deleteById(ruleUuid);
    }

    @Override
    @Transactional
    public void enableRule(String ruleUuid) {
        AlertRule rule = alertRuleRepository.findById(ruleUuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.ALERT_RULE_NOT_FOUND));
        rule.enable();
        alertRuleRepository.save(rule);
    }

    @Override
    @Transactional
    public void disableRule(String ruleUuid) {
        AlertRule rule = alertRuleRepository.findById(ruleUuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.ALERT_RULE_NOT_FOUND));
        rule.disable();
        alertRuleRepository.save(rule);
    }
}
