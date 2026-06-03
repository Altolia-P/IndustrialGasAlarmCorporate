package com.niit.industrialgasalarmcorporate.infrastructure.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.niit.industrialgasalarmcorporate.domain.alert.Alert;
import com.niit.industrialgasalarmcorporate.domain.alert.AlertRepository;
import com.niit.industrialgasalarmcorporate.domain.alert.AlertStatus;
import com.niit.industrialgasalarmcorporate.domain.device.Device;
import com.niit.industrialgasalarmcorporate.domain.device.DeviceRepository;
import com.niit.industrialgasalarmcorporate.domain.device.DeviceStatus;
import com.niit.industrialgasalarmcorporate.infrastructure.feign.DeviceDataClient;
import com.niit.industrialgasalarmcorporate.infrastructure.feign.dto.DeviceDataPointFeignVO;
import com.niit.industrialgasalarmcorporate.infrastructure.websocket.DashboardWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class DashboardPushScheduler {

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final int MAX_TREND_DEVICES = 10;

    private final DashboardWebSocketHandler wsHandler;
    private final DeviceRepository deviceRepository;
    private final AlertRepository alertRepository;
    private final DeviceDataClient deviceDataClient;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 5_000)
    public void pushDashboardData() {
        if (wsHandler.getConnectionCount() == 0) {
            return;
        }

        // Cache the global admin payload — built once for all admin sessions
        final Map<String, Object>[] adminPayload = new Map[]{null};

        wsHandler.broadcastScoped((userUuid, role) -> {
            try {
                Map<String, Object> payload;
                if ("ADMIN".equals(role)) {
                    if (adminPayload[0] == null) {
                        adminPayload[0] = buildScopedPayload(null);
                    }
                    payload = adminPayload[0];
                } else {
                    payload = buildScopedPayload(userUuid);
                }
                return objectMapper.writeValueAsString(payload);
            } catch (Exception e) {
                log.warn("构建用户大屏数据失败: userUuid={}", userUuid, e);
                return null;
            }
        });
    }

    private Map<String, Object> buildScopedPayload(String customerUuid) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("type", "dashboard:refresh");
        payload.put("timestamp", DTF.format(LocalDateTime.now()));

        List<Device> devices = customerUuid != null
                ? deviceRepository.findByCustomerUuid(customerUuid)
                : deviceRepository.findAll();

        // 设备状态列表
        List<Map<String, Object>> deviceList = devices.stream().map(d -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("deviceUuid", d.getDeviceUuid());
            m.put("name", d.getName());
            m.put("status", d.getStatus().name());
            m.put("gasType", d.getGasType() != null ? d.getGasType().name() : "—");
            m.put("installLocation", d.getInstallLocation() != null ? d.getInstallLocation() : "—");
            return m;
        }).collect(Collectors.toList());
        payload.put("devices", deviceList);

        // 实时浓度数据点
        List<Map<String, Object>> dataPoints = new ArrayList<>();
        for (Device d : devices.stream().limit(MAX_TREND_DEVICES).collect(Collectors.toList())) {
            try {
                DeviceDataPointFeignVO latest = deviceDataClient.getLatest(d.getDeviceUuid());
                if (latest != null && latest.getConcentration() != null) {
                    Map<String, Object> dp = new LinkedHashMap<>();
                    dp.put("deviceUuid", d.getDeviceUuid());
                    dp.put("deviceName", d.getName());
                    dp.put("concentration", latest.getConcentration());
                    dp.put("timestamp", latest.getTimestamp() != null ? latest.getTimestamp() : DTF.format(LocalDateTime.now()));
                    dataPoints.add(dp);
                }
            } catch (Exception e) {
                log.debug("获取设备实时数据失败: {}", e.getMessage());
            }
        }
        payload.put("dataPoints", dataPoints);

        // 在线率
        List<Device> scopedDevices = customerUuid != null
                ? deviceRepository.findByCustomerUuid(customerUuid)
                : Collections.emptyList();
        long online, offline, maintenance;
        if (customerUuid != null) {
            online = scopedDevices.stream().filter(d -> d.getStatus() == DeviceStatus.NORMAL || d.getStatus() == DeviceStatus.ABNORMAL).count();
            offline = scopedDevices.stream().filter(d -> d.getStatus() == DeviceStatus.OFFLINE).count();
            maintenance = scopedDevices.stream().filter(d -> d.getStatus() == DeviceStatus.MAINTENANCE).count();
        } else {
            online = deviceRepository.countByStatus(DeviceStatus.NORMAL) + deviceRepository.countByStatus(DeviceStatus.ABNORMAL);
            offline = deviceRepository.countByStatus(DeviceStatus.OFFLINE);
            maintenance = deviceRepository.countByStatus(DeviceStatus.MAINTENANCE);
        }
        payload.put("onlineCount", (int) online);
        payload.put("offlineCount", (int) offline);
        payload.put("maintenanceCount", (int) maintenance);
        payload.put("totalCount", (int) (online + offline + maintenance));

        // 近期告警
        Map<String, String> deviceNameMap = devices.stream()
                .collect(Collectors.toMap(Device::getDeviceUuid, Device::getName, (a, b) -> a));
        List<String> deviceUuids = devices.stream().map(Device::getDeviceUuid).collect(Collectors.toList());
        List<Alert> alerts = deviceUuids.isEmpty()
                ? Collections.emptyList()
                : alertRepository.findByDeviceUuids(deviceUuids, 20);
        List<Map<String, Object>> alertList = alerts.stream().map(a -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("alertUuid", a.getAlertUuid());
            m.put("deviceUuid", a.getDeviceUuid());
            m.put("deviceName", deviceNameMap.getOrDefault(a.getDeviceUuid(), "—"));
            m.put("severity", a.getSeverity().name());
            m.put("alertType", a.getAlertType() != null ? a.getAlertType().name() : "—");
            m.put("message", a.getMessage());
            m.put("concentration", a.getConcentration() != null ? a.getConcentration().toPlainString() : "—");
            m.put("triggeredAt", DTF.format(a.getTriggeredAt()));
            return m;
        }).collect(Collectors.toList());
        payload.put("alerts", alertList);

        // pending alert count
        int pendingAlertCount;
        if (customerUuid != null) {
            pendingAlertCount = deviceUuids.stream()
                    .mapToInt(uuid -> (int) alertRepository.countPendingByDevice(uuid))
                    .sum();
        } else {
            pendingAlertCount = (int) alertRepository.countByStatus(AlertStatus.PENDING);
        }
        payload.put("pendingAlertCount", pendingAlertCount);

        return payload;
    }
}
