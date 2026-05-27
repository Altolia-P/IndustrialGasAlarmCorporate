package com.niit.industrialgasalarmcorporate.application.alert.service;

import com.niit.industrialgasalarmcorporate.application.alert.dto.CreateAlertRuleDTO;
import com.niit.industrialgasalarmcorporate.application.alert.dto.UpdateAlertRuleDTO;
import com.niit.industrialgasalarmcorporate.application.alert.vo.AlertRuleVO;

import java.util.List;

public interface AlertRuleService {

    List<AlertRuleVO> findAll();

    AlertRuleVO getRule(String ruleUuid);

    AlertRuleVO createRule(CreateAlertRuleDTO dto);

    AlertRuleVO updateRule(String ruleUuid, UpdateAlertRuleDTO dto);

    void deleteRule(String ruleUuid);

    void enableRule(String ruleUuid);

    void disableRule(String ruleUuid);
}
