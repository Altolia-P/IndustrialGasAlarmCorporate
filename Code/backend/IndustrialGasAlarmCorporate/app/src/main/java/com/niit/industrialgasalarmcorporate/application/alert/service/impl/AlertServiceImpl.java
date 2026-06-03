package com.niit.industrialgasalarmcorporate.application.alert.service.impl;

import com.niit.industrialgasalarmcorporate.application.alert.service.AlertService;
import com.niit.industrialgasalarmcorporate.application.alert.vo.AlertVO;
import com.niit.industrialgasalarmcorporate.assembler.AlertAssembler;
import com.niit.industrialgasalarmcorporate.common.base.Page;
import com.niit.industrialgasalarmcorporate.common.enums.ErrorCode;
import com.niit.industrialgasalarmcorporate.common.exception.BusinessException;
import com.niit.industrialgasalarmcorporate.domain.alert.Alert;
import com.niit.industrialgasalarmcorporate.domain.alert.AlertRepository;
import com.niit.industrialgasalarmcorporate.domain.auth.User;
import com.niit.industrialgasalarmcorporate.domain.auth.UserRepository;
import com.niit.industrialgasalarmcorporate.domain.device.Device;
import com.niit.industrialgasalarmcorporate.domain.device.DeviceRepository;
import com.niit.industrialgasalarmcorporate.infrastructure.redis.DashboardCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertServiceImpl implements AlertService {

    private final AlertRepository alertRepository;
    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;
    private final DashboardCacheRepository dashboardCacheRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<AlertVO> findAlerts(String deviceUuid, String alertType, String severity,
                                    String status, int page, int size) {
        Page<Alert> domainPage = alertRepository.findAllWithFilter(
                deviceUuid, alertType, severity, status, page, size);
        List<AlertVO> vos = domainPage.getContent().stream()
                .map(AlertAssembler::toVO)
                .collect(Collectors.toList());
        enrichBatch(vos);
        return new Page<>(vos, domainPage.getTotalElements(), domainPage.getSize(), domainPage.getNumber());
    }

    @Override
    @Transactional(readOnly = true)
    public AlertVO getAlert(String alertUuid) {
        Alert alert = alertRepository.findById(alertUuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.ALERT_NOT_FOUND));
        AlertVO vo = AlertAssembler.toVO(alert);
        enrich(vo);
        return vo;
    }

    @Override
    @Transactional
    public void confirmAlert(String alertUuid, String confirmedBy) {
        Alert alert = alertRepository.findById(alertUuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.ALERT_NOT_FOUND));
        alert.confirm(confirmedBy);
        alertRepository.save(alert);
        evictDashboardCache();
    }

    @Override
    @Transactional
    public void resolveAlert(String alertUuid, String resolvedBy) {
        Alert alert = alertRepository.findById(alertUuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.ALERT_NOT_FOUND));
        alert.resolve(resolvedBy);
        alertRepository.save(alert);
        evictDashboardCache();
    }

    @Override
    @Transactional
    public void closeAlert(String alertUuid) {
        Alert alert = alertRepository.findById(alertUuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.ALERT_NOT_FOUND));
        alert.close();
        alertRepository.save(alert);
        evictDashboardCache();
    }

    private void evictDashboardCache() {
        try {
            dashboardCacheRepository.evict("dashboard:stats");
        } catch (Exception e) {
            log.debug("Dashboard缓存失效失败: {}", e.getMessage());
        }
    }

    private void enrichBatch(List<AlertVO> vos) {
        if (vos.isEmpty()) return;
        Set<String> deviceUuids = vos.stream().map(AlertVO::getDeviceUuid).collect(Collectors.toSet());
        Map<String, Device> deviceMap = deviceRepository.findByIds(deviceUuids).stream()
                .collect(Collectors.toMap(Device::getDeviceUuid, d -> d, (a, b) -> a));
        Set<String> customerUuids = deviceMap.values().stream()
                .map(Device::getCustomerUuid).filter(c -> c != null && !c.isBlank()).collect(Collectors.toSet());
        Map<String, User> userMap = customerUuids.isEmpty() ? new HashMap<>()
                : userRepository.findByIds(customerUuids).stream()
                .collect(Collectors.toMap(User::getUserUuid, u -> u, (a, b) -> a));
        for (AlertVO vo : vos) {
            Device device = deviceMap.get(vo.getDeviceUuid());
            if (device != null) {
                vo.setDeviceName(device.getName());
                vo.setDeviceSerialNumber(device.getSerialNumber());
                String customerUuid = device.getCustomerUuid();
                if (customerUuid != null && !customerUuid.isBlank()) {
                    vo.setCustomerUuid(customerUuid);
                    User user = userMap.get(customerUuid);
                    if (user != null) {
                        vo.setCustomerName(user.getCompany());
                        vo.setCustomerPhone(user.getPhone());
                    }
                }
            }
        }
    }

    private void enrich(AlertVO vo) {
        deviceRepository.findById(vo.getDeviceUuid()).ifPresent(device -> {
            vo.setDeviceName(device.getName());
            vo.setDeviceSerialNumber(device.getSerialNumber());
            String customerUuid = device.getCustomerUuid();
            if (customerUuid != null && !customerUuid.isBlank()) {
                vo.setCustomerUuid(customerUuid);
                userRepository.findById(customerUuid).ifPresent(user -> {
                    vo.setCustomerName(user.getCompany());
                    vo.setCustomerPhone(user.getPhone());
                });
            }
        });
    }
}
