package com.niit.industrialgasalarmcorporate.infrastructure.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niit.industrialgasalarmcorporate.domain.alert.Alert;
import com.niit.industrialgasalarmcorporate.domain.alert.AlertRepository;
import com.niit.industrialgasalarmcorporate.domain.alert.AlertRuleType;
import com.niit.industrialgasalarmcorporate.domain.alert.AlertSeverity;
import com.niit.industrialgasalarmcorporate.domain.alert.AlertStatus;
import com.niit.industrialgasalarmcorporate.infrastructure.repository.mapper.AlertMapper;
import com.niit.industrialgasalarmcorporate.infrastructure.repository.po.AlertPO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class AlertRepositoryImpl implements AlertRepository {

    private final AlertMapper alertMapper;

    @Override
    public Optional<Alert> findById(String alertUuid) {
        AlertPO po = alertMapper.selectById(alertUuid);
        if (po == null) {
            return Optional.empty();
        }
        return Optional.of(toDomain(po));
    }

    @Override
    public com.niit.industrialgasalarmcorporate.common.base.Page<Alert> findByDeviceUuid(
            String deviceUuid, int page, int size) {
        LambdaQueryWrapper<AlertPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AlertPO::getDeviceUuid, deviceUuid)
                .orderByDesc(AlertPO::getTriggeredAt);
        Page<AlertPO> mpPage = new Page<>(page, size);
        Page<AlertPO> result = alertMapper.selectPage(mpPage, wrapper);
        List<Alert> alerts = result.getRecords().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
        return new com.niit.industrialgasalarmcorporate.common.base.Page<>(
                alerts, result.getTotal(), (int) result.getSize(), (int) result.getCurrent());
    }

    @Override
    public com.niit.industrialgasalarmcorporate.common.base.Page<Alert> findAllWithFilter(
            String deviceUuid, String alertType, String severity, String status, int page, int size) {
        LambdaQueryWrapper<AlertPO> wrapper = new LambdaQueryWrapper<>();
        if (deviceUuid != null && !deviceUuid.isBlank()) {
            wrapper.eq(AlertPO::getDeviceUuid, deviceUuid);
        }
        if (alertType != null && !alertType.isBlank()) {
            wrapper.eq(AlertPO::getAlertType, alertType);
        }
        if (severity != null && !severity.isBlank()) {
            wrapper.eq(AlertPO::getSeverity, severity);
        }
        if (status != null && !status.isBlank()) {
            wrapper.eq(AlertPO::getStatus, status);
        }
        wrapper.orderByDesc(AlertPO::getTriggeredAt);
        Page<AlertPO> mpPage = new Page<>(page, size);
        Page<AlertPO> result = alertMapper.selectPage(mpPage, wrapper);
        List<Alert> alerts = result.getRecords().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
        return new com.niit.industrialgasalarmcorporate.common.base.Page<>(
                alerts, result.getTotal(), (int) result.getSize(), (int) result.getCurrent());
    }

    @Override
    public void save(Alert alert) {
        AlertPO po = toPO(alert);
        AlertPO existing = alertMapper.selectById(alert.getAlertUuid());
        if (existing != null) {
            alertMapper.updateById(po);
        } else {
            alertMapper.insert(po);
        }
    }

    @Override
    public long countPendingByDevice(String deviceUuid) {
        LambdaQueryWrapper<AlertPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AlertPO::getDeviceUuid, deviceUuid)
                .eq(AlertPO::getStatus, AlertStatus.PENDING.name());
        return alertMapper.selectCount(wrapper);
    }

    @Override
    public long countByStatus(AlertStatus status) {
        LambdaQueryWrapper<AlertPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AlertPO::getStatus, status.name());
        return alertMapper.selectCount(wrapper);
    }

    @Override
    public long countBySeverity(AlertSeverity severity) {
        LambdaQueryWrapper<AlertPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AlertPO::getSeverity, severity.name());
        return alertMapper.selectCount(wrapper);
    }

    @Override
    public long countToday() {
        LocalDate today = LocalDate.now();
        LambdaQueryWrapper<AlertPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(AlertPO::getTriggeredAt, today.atStartOfDay())
                .lt(AlertPO::getTriggeredAt, today.plusDays(1).atStartOfDay());
        return alertMapper.selectCount(wrapper);
    }

    @Override
    public Map<LocalDate, Long> countByDay(LocalDate from, LocalDate to) {
        QueryWrapper<AlertPO> wrapper = new QueryWrapper<>();
        wrapper.select("DATE(triggered_at) as day", "COUNT(*) as cnt")
                .ge("triggered_at", from.atStartOfDay())
                .lt("triggered_at", to.plusDays(1).atStartOfDay())
                .groupBy("DATE(triggered_at)")
                .orderByAsc("day");
        List<Map<String, Object>> rows = alertMapper.selectMaps(wrapper);
        Map<LocalDate, Long> result = new LinkedHashMap<>();
        for (LocalDate d = from; !d.isAfter(to); d = d.plusDays(1)) {
            result.put(d, 0L);
        }
        for (Map<String, Object> row : rows) {
            Object dayObj = row.get("day");
            Object cntObj = row.get("cnt");
            if (dayObj != null && cntObj != null) {
                LocalDate day = LocalDate.parse(dayObj.toString().substring(0, 10));
                long cnt = ((Number) cntObj).longValue();
                result.put(day, cnt);
            }
        }
        return result;
    }

    private Alert toDomain(AlertPO po) {
        return new Alert(
                po.getAlertUuid(),
                po.getDeviceUuid(),
                po.getRuleUuid(),
                AlertRuleType.valueOf(po.getAlertType()),
                AlertSeverity.valueOf(po.getSeverity()),
                po.getConcentration(),
                po.getThreshold(),
                po.getMessage(),
                AlertStatus.valueOf(po.getStatus()),
                po.getTriggeredAt(),
                po.getConfirmedAt(),
                po.getConfirmedBy(),
                po.getResolvedAt(),
                po.getResolvedBy(),
                po.getWorkOrderUuid(),
                po.getCreatedAt(),
                po.getUpdatedAt()
        );
    }

    private AlertPO toPO(Alert alert) {
        AlertPO po = new AlertPO();
        po.setAlertUuid(alert.getAlertUuid());
        po.setDeviceUuid(alert.getDeviceUuid());
        po.setRuleUuid(alert.getRuleUuid());
        po.setAlertType(alert.getAlertType().name());
        po.setSeverity(alert.getSeverity().name());
        po.setConcentration(alert.getConcentration());
        po.setThreshold(alert.getThreshold());
        po.setMessage(alert.getMessage());
        po.setStatus(alert.getStatus().name());
        po.setTriggeredAt(alert.getTriggeredAt());
        po.setConfirmedAt(alert.getConfirmedAt());
        po.setConfirmedBy(alert.getConfirmedBy());
        po.setResolvedAt(alert.getResolvedAt());
        po.setResolvedBy(alert.getResolvedBy());
        po.setWorkOrderUuid(alert.getWorkOrderUuid());
        return po;
    }
}
