package com.niit.industrialgasalarmcorporate.interfaces.dashboard;

import com.niit.industrialgasalarmcorporate.application.dashboard.vo.DashboardAlertVO;
import com.niit.industrialgasalarmcorporate.application.dashboard.vo.DashboardDeviceVO;
import com.niit.industrialgasalarmcorporate.application.dashboard.vo.DashboardOverviewVO;
import com.niit.industrialgasalarmcorporate.application.device.vo.DeviceDataPointVO;
import com.niit.industrialgasalarmcorporate.common.base.Result;
import com.niit.industrialgasalarmcorporate.domain.alert.Alert;
import com.niit.industrialgasalarmcorporate.domain.alert.AlertRepository;
import com.niit.industrialgasalarmcorporate.domain.alert.AlertStatus;
import com.niit.industrialgasalarmcorporate.domain.auth.User;
import com.niit.industrialgasalarmcorporate.domain.auth.UserRepository;
import com.niit.industrialgasalarmcorporate.domain.device.Device;
import com.niit.industrialgasalarmcorporate.domain.device.DeviceRepository;
import com.niit.industrialgasalarmcorporate.domain.device.DeviceStatus;
import com.niit.industrialgasalarmcorporate.infrastructure.feign.DeviceDataClient;
import com.niit.industrialgasalarmcorporate.infrastructure.feign.dto.DeviceDataPointFeignVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.lang.management.ManagementFactory;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardDeviceDataController {

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final DeviceDataClient deviceDataClient;
    private final DeviceRepository deviceRepository;
    private final AlertRepository alertRepository;
    private final UserRepository userRepository;

    // ─── 数据点查询（代理 device-collector）─────────────────────────────────

    @GetMapping("/device-data")
    public Result<List<DeviceDataPointVO>> getDataPoints(@RequestParam String deviceUuid,
                                                          @RequestParam(required = false) String from,
                                                          @RequestParam(required = false) String to) {
        List<DeviceDataPointFeignVO> feignVOs = deviceDataClient.getDataPoints(deviceUuid, from, to);
        if (feignVOs == null) {
            return Result.ok(Collections.emptyList());
        }
        return Result.ok(feignVOs.stream().map(this::toVO).collect(Collectors.toList()));
    }

    @GetMapping("/device-data/latest")
    public Result<DeviceDataPointVO> getLatest(@RequestParam String deviceUuid) {
        DeviceDataPointFeignVO feignVO = deviceDataClient.getLatest(deviceUuid);
        if (feignVO == null) {
            return Result.ok(null);
        }
        return Result.ok(toVO(feignVO));
    }

    // ─── 大屏概览（FR-4.1 / FR-4.2）───────────────────────────────────────

    @GetMapping("/overview")
    public Result<DashboardOverviewVO> getOverview(@RequestAttribute(required = false) String userUuid) {
        List<Device> devices = resolveDevices(userUuid);
        int total = devices.size();
        long online = devices.stream()
                .filter(d -> d.getStatus() == DeviceStatus.NORMAL || d.getStatus() == DeviceStatus.ABNORMAL)
                .count();
        int alertCount;
        if (isAdmin()) {
            alertCount = (int) alertRepository.countByStatus(AlertStatus.PENDING);
        } else {
            alertCount = devices.stream()
                    .mapToInt(d -> (int) alertRepository.countPendingByDevice(d.getDeviceUuid()))
                    .sum();
        }

        long todayDataPoints = 0;
        String avgConcentration = "—";
        try {
            var stats = deviceDataClient.getStats();
            if (stats != null) {
                todayDataPoints = stats.getTodayDataPoints();
                avgConcentration = stats.getAvgConcentration() != null ? stats.getAvgConcentration() : "—";
            }
        } catch (Exception e) {
            log.debug("获取统计数据失败: {}", e.getMessage());
        }

        String uptime = formatUptime(ManagementFactory.getRuntimeMXBean().getUptime());

        return Result.ok(new DashboardOverviewVO(
                (int) online, total, alertCount, todayDataPoints, avgConcentration, uptime));
    }

    // ─── 近期告警（FR-4.3）─────────────────────────────────────────────────

    @GetMapping("/alerts")
    public Result<List<DashboardAlertVO>> getAlerts(@RequestParam(defaultValue = "20") int limit,
                                                     @RequestAttribute(required = false) String userUuid) {
        List<Device> devices = resolveDevices(userUuid);

        Map<String, String> deviceNameMap = devices.stream()
                .collect(Collectors.toMap(Device::getDeviceUuid, Device::getName, (a, b) -> a));

        List<Alert> alerts = alertRepository.findByDeviceUuids(
                devices.stream().map(Device::getDeviceUuid).collect(Collectors.toList()),
                limit);

        List<DashboardAlertVO> vos = alerts.stream()
                .map(a -> new DashboardAlertVO(
                        a.getAlertUuid(),
                        a.getDeviceUuid(),
                        deviceNameMap.getOrDefault(a.getDeviceUuid(), "—"),
                        a.getSeverity().name(),
                        a.getAlertType().name(),
                        a.getConcentration().toPlainString(),
                        a.getMessage(),
                        DTF.format(a.getTriggeredAt())))
                .collect(Collectors.toList());

        return Result.ok(vos);
    }

    // ─── 设备状态列表（FR-4.4 / FR-4.14 CUSTOMER 隔离）─────────────────────

    @GetMapping("/devices")
    public Result<List<DashboardDeviceVO>> getDevices(@RequestAttribute(required = false) String userUuid) {
        List<Device> devices = resolveDevices(userUuid);

        java.util.Set<String> customerUuids = devices.stream()
                .map(Device::getCustomerUuid)
                .filter(uuid -> uuid != null && !uuid.isBlank())
                .collect(Collectors.toSet());
        Map<String, String> customerNameMap = customerUuids.isEmpty()
                ? Collections.emptyMap()
                : userRepository.findByIds(customerUuids).stream()
                        .collect(Collectors.toMap(User::getUserUuid,
                                u -> u.getCompany() != null ? u.getCompany() : u.getUsername(),
                                (a, b) -> a));

        List<DashboardDeviceVO> vos = devices.stream()
                .map(d -> {
                    String latestConcentration = "—";
                    try {
                        DeviceDataPointFeignVO latest = deviceDataClient.getLatest(d.getDeviceUuid());
                        if (latest != null && latest.getConcentration() != null) {
                            latestConcentration = latest.getConcentration();
                        }
                    } catch (Exception e) {
                        log.debug("获取设备最新浓度失败: {}", e.getMessage());
                    }
                    String cuuid = d.getCustomerUuid();
                    return new DashboardDeviceVO(
                            d.getDeviceUuid(),
                            d.getName(),
                            d.getModel(),
                            d.getGasType() != null ? d.getGasType().name() : "—",
                            d.getInstallLocation() != null ? d.getInstallLocation() : "—",
                            d.getStatus().name(),
                            latestConcentration,
                            cuuid != null ? cuuid : "",
                            cuuid != null ? customerNameMap.getOrDefault(cuuid, "—") : "—");
                })
                .collect(Collectors.toList());

        return Result.ok(vos);
    }

    // ─── CUSTOMER 数据隔离（FR-4.14）──────────────────────────────────────

    /**
     * ADMIN 角色返回全部设备；CUSTOMER 角色只返回 customerUuid == userUuid 的设备。
     * <p>
     * 系统约定：CUSTOMER 用户注册后，其 userUuid 即为其 customerUuid，设备通过
     * customerUuid 关联到所属客户。
     */
    private List<Device> resolveDevices(String userUuid) {
        if (!isAdmin()) {
            if (userUuid == null || userUuid.isBlank()) return Collections.emptyList();
            return deviceRepository.findByCustomerUuid(userUuid);
        }
        return deviceRepository.findAll();
    }

    private boolean isAdmin() {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals("ROLE_ADMIN"));
    }

    // ─── helper ───────────────────────────────────────────────────────────

    private DeviceDataPointVO toVO(DeviceDataPointFeignVO feignVO) {
        DeviceDataPointVO vo = new DeviceDataPointVO();
        vo.setDeviceUuid(feignVO.getDeviceUuid());
        vo.setTimestamp(feignVO.getTimestamp());
        vo.setConcentration(feignVO.getConcentration());
        vo.setBattery(feignVO.getBattery());
        vo.setTemperature(feignVO.getTemperature());
        vo.setHumidity(feignVO.getHumidity());
        vo.setSignalStrength(feignVO.getSignalStrength());
        vo.setCreatedAt(feignVO.getCreatedAt());
        return vo;
    }

    private String formatUptime(long millis) {
        long seconds = millis / 1000;
        long h = seconds / 3600;
        long m = (seconds % 3600) / 60;
        return String.format("%dh %dm", h, m);
    }
}
