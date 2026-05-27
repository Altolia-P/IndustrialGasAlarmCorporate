package com.niit.industrialgasalarmcorporate.application.dashboard.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.niit.industrialgasalarmcorporate.application.dashboard.service.DashboardService;
import com.niit.industrialgasalarmcorporate.application.dashboard.vo.AlertTrendItem;
import com.niit.industrialgasalarmcorporate.application.dashboard.vo.DashboardStatsVO;
import com.niit.industrialgasalarmcorporate.domain.alert.AlertRepository;
import com.niit.industrialgasalarmcorporate.domain.alert.AlertSeverity;
import com.niit.industrialgasalarmcorporate.domain.alert.AlertStatus;
import com.niit.industrialgasalarmcorporate.domain.device.DeviceRepository;
import com.niit.industrialgasalarmcorporate.domain.device.DeviceStatus;
import com.niit.industrialgasalarmcorporate.infrastructure.redis.DashboardCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final DeviceRepository deviceRepository;
    private final AlertRepository alertRepository;
    private final DashboardCacheRepository dashboardCacheRepository;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Override
    @Transactional(readOnly = true)
    public DashboardStatsVO getStats() {
        var cached = dashboardCacheRepository.getStats();
        if (cached.isPresent()) {
            try {
                return objectMapper.readValue(cached.get(), DashboardStatsVO.class);
            } catch (JsonProcessingException e) {
                log.warn("Dashboard缓存解析失败，重新计算");
            }
        }

        DashboardStatsVO stats = computeStats();
        try {
            dashboardCacheRepository.setStats(objectMapper.writeValueAsString(stats));
        } catch (JsonProcessingException e) {
            log.warn("Dashboard缓存写入失败: {}", e.getMessage());
        }
        return stats;
    }

    private DashboardStatsVO computeStats() {
        DashboardStatsVO stats = new DashboardStatsVO();

        stats.setNormalDevices((int) deviceRepository.countByStatus(DeviceStatus.NORMAL));
        stats.setAbnormalDevices((int) deviceRepository.countByStatus(DeviceStatus.ABNORMAL));
        stats.setOfflineDevices((int) deviceRepository.countByStatus(DeviceStatus.OFFLINE));
        stats.setMaintenanceDevices((int) deviceRepository.countByStatus(DeviceStatus.MAINTENANCE));
        stats.setTotalDevices(stats.getNormalDevices() + stats.getAbnormalDevices()
                + stats.getOfflineDevices() + stats.getMaintenanceDevices());

        stats.setPendingAlerts((int) alertRepository.countByStatus(AlertStatus.PENDING));
        stats.setCriticalAlerts((int) alertRepository.countBySeverity(AlertSeverity.CRITICAL));
        stats.setWarningAlerts((int) alertRepository.countBySeverity(AlertSeverity.WARNING));
        stats.setAlertsToday((int) alertRepository.countToday());

        long critical = alertRepository.countBySeverity(AlertSeverity.CRITICAL);
        long warning = alertRepository.countBySeverity(AlertSeverity.WARNING);
        long info = alertRepository.countBySeverity(AlertSeverity.INFO);
        stats.setTotalAlerts((int) (critical + warning + info));

        LocalDate today = LocalDate.now();
        LocalDate sevenDaysAgo = today.minusDays(6);
        Map<LocalDate, Long> trendMap = alertRepository.countByDay(sevenDaysAgo, today);
        List<AlertTrendItem> trend = trendMap.entrySet().stream()
                .map(e -> new AlertTrendItem(e.getKey().toString(), e.getValue()))
                .collect(Collectors.toList());
        stats.setAlertTrend(trend);

        return stats;
    }
}
