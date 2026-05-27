package com.niit.industrialgasalarmcorporate.application.alert.service.impl;

import com.niit.industrialgasalarmcorporate.application.alert.service.AlertService;
import com.niit.industrialgasalarmcorporate.application.alert.vo.AlertVO;
import com.niit.industrialgasalarmcorporate.assembler.AlertAssembler;
import com.niit.industrialgasalarmcorporate.common.base.Page;
import com.niit.industrialgasalarmcorporate.common.enums.ErrorCode;
import com.niit.industrialgasalarmcorporate.common.exception.BusinessException;
import com.niit.industrialgasalarmcorporate.domain.alert.Alert;
import com.niit.industrialgasalarmcorporate.domain.alert.AlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlertServiceImpl implements AlertService {

    private final AlertRepository alertRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<AlertVO> findAlerts(String deviceUuid, String alertType, String severity,
                                    String status, int page, int size) {
        Page<Alert> domainPage = alertRepository.findAllWithFilter(
                deviceUuid, alertType, severity, status, page, size);
        return new Page<>(
                domainPage.getContent().stream().map(AlertAssembler::toVO).collect(Collectors.toList()),
                domainPage.getTotalElements(),
                domainPage.getSize(),
                domainPage.getNumber()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public AlertVO getAlert(String alertUuid) {
        Alert alert = alertRepository.findById(alertUuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.ALERT_NOT_FOUND));
        return AlertAssembler.toVO(alert);
    }

    @Override
    @Transactional
    public void confirmAlert(String alertUuid, String confirmedBy) {
        Alert alert = alertRepository.findById(alertUuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.ALERT_NOT_FOUND));
        alert.confirm(confirmedBy);
        alertRepository.save(alert);
    }

    @Override
    @Transactional
    public void resolveAlert(String alertUuid, String resolvedBy) {
        Alert alert = alertRepository.findById(alertUuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.ALERT_NOT_FOUND));
        alert.resolve(resolvedBy);
        alertRepository.save(alert);
    }

    @Override
    @Transactional
    public void closeAlert(String alertUuid) {
        Alert alert = alertRepository.findById(alertUuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.ALERT_NOT_FOUND));
        alert.close();
        alertRepository.save(alert);
    }
}
