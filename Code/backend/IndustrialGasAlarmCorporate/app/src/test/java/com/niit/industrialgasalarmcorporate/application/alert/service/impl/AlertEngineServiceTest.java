package com.niit.industrialgasalarmcorporate.application.alert.service.impl;

import com.niit.industrialgasalarmcorporate.application.workorder.dto.CreateWorkOrderDTO;
import com.niit.industrialgasalarmcorporate.application.workorder.service.WorkOrderService;
import com.niit.industrialgasalarmcorporate.application.workorder.vo.WorkOrderVO;
import com.niit.industrialgasalarmcorporate.domain.alert.*;
import com.niit.industrialgasalarmcorporate.domain.device.Device;
import com.niit.industrialgasalarmcorporate.domain.device.DeviceDataPoint;
import com.niit.industrialgasalarmcorporate.domain.device.DeviceRepository;
import com.niit.industrialgasalarmcorporate.domain.device.DeviceStatus;
import com.niit.industrialgasalarmcorporate.domain.device.GasType;
import com.niit.industrialgasalarmcorporate.domain.event.EventBus;
import com.niit.industrialgasalarmcorporate.infrastructure.redis.AlertSuppressRepository;
import com.niit.industrialgasalarmcorporate.infrastructure.redis.DeviceDataWindowRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AlertEngineService 报警引擎")
class AlertEngineServiceTest {

    @Mock private DeviceRepository deviceRepository;
    @Mock private AlertRuleRepository alertRuleRepository;
    @Mock private AlertRepository alertRepository;
    @Mock private DeviceDataWindowRepository deviceDataWindowRepository;
    @Mock private AlertSuppressRepository alertSuppressRepository;
    @Mock private EventBus eventBus;
    @Mock private WorkOrderService workOrderService;

    @InjectMocks
    private AlertEngineServiceImpl alertEngineService;

    private Device device;
    private AlertRule rule;
    private DeviceDataPoint dataPoint;
    private static final String DEVICE_UUID = "device-001";
    private static final String RULE_UUID = "rule-001";

    @BeforeEach
    void setUp() {
        device = new Device(DEVICE_UUID, "SN-001", "测试设备", "GT-M4", "cust-001",
                "A区", null, GasType.CH4, new BigDecimal("0"), new BigDecimal("5.0"),
                new BigDecimal("1.0"), DeviceStatus.NORMAL,
                LocalDateTime.now(), LocalDateTime.now());

        rule = new AlertRule(RULE_UUID, "阈值规则", DEVICE_UUID, AlertRuleType.THRESHOLD,
                "CH4", new BigDecimal("1.0"), 10, AlertSeverity.WARNING,
                false, true, LocalDateTime.now(), LocalDateTime.now());

        dataPoint = new DeviceDataPoint(DEVICE_UUID, LocalDateTime.now(),
                new BigDecimal("2.0"), null, null, null, 80);
    }

    @Nested
    @DisplayName("evaluate — 阈值判断")
    class ThresholdEvaluation {

        @Test
        @DisplayName("设备不存在时静默返回")
        void shouldReturnSilentlyWhenDeviceNotFound() {
            when(deviceRepository.findById(DEVICE_UUID)).thenReturn(Optional.empty());

            alertEngineService.evaluate(dataPoint);

            verify(alertRuleRepository, never()).findByDeviceUuid(any());
        }

        @Test
        @DisplayName("规则不匹配时跳过评估")
        void shouldSkipWhenRuleDoesNotMatch() {
            when(deviceRepository.findById(DEVICE_UUID)).thenReturn(Optional.of(device));
            List<AlertRule> rules = new ArrayList<>();
            rules.add(rule);
            when(alertRuleRepository.findByDeviceUuid(DEVICE_UUID)).thenReturn(rules);
            when(alertRuleRepository.findAllGlobal()).thenReturn(List.of());

            // 用 H2S 类型的数据点，规则只匹配 CH4
            Device h2sDevice = new Device(DEVICE_UUID, "SN-002", "H2S设备", "GT-H2S", "cust-001",
                    "B区", null, GasType.H2S, new BigDecimal("0"), new BigDecimal("5.0"),
                    new BigDecimal("1.0"), DeviceStatus.NORMAL,
                    LocalDateTime.now(), LocalDateTime.now());
            when(deviceRepository.findById(DEVICE_UUID)).thenReturn(Optional.of(h2sDevice));

            DeviceDataPoint h2sPoint = new DeviceDataPoint(DEVICE_UUID, LocalDateTime.now(),
                    new BigDecimal("2.0"), null, null, null, 80);
            alertEngineService.evaluate(h2sPoint);

            verify(alertRepository, never()).save(any());
        }

        @Test
        @DisplayName("浓度未超过阈值时不触发报警")
        void shouldNotTriggerWhenBelowThreshold() {
            when(deviceRepository.findById(DEVICE_UUID)).thenReturn(Optional.of(device));
            List<AlertRule> rules = new ArrayList<>();
            rules.add(rule);
            when(alertRuleRepository.findByDeviceUuid(DEVICE_UUID)).thenReturn(rules);
            when(alertRuleRepository.findAllGlobal()).thenReturn(List.of());

            DeviceDataPoint lowPoint = new DeviceDataPoint(DEVICE_UUID, LocalDateTime.now(),
                    new BigDecimal("0.5"), null, null, null, 80);
            alertEngineService.evaluate(lowPoint);

            verify(deviceDataWindowRepository, never()).addDataPoint(any(), anyDouble(), anyLong());
            verify(alertRepository, never()).save(any());
        }

        @Test
        @DisplayName("浓度超过阈值且窗口计数>=1 时触发报警")
        void shouldTriggerAlertWhenThresholdExceeded() {
            when(deviceRepository.findById(DEVICE_UUID)).thenReturn(Optional.of(device));
            List<AlertRule> rules = new ArrayList<>();
            rules.add(rule);
            when(alertRuleRepository.findByDeviceUuid(DEVICE_UUID)).thenReturn(rules);
            when(alertRuleRepository.findAllGlobal()).thenReturn(List.of());
            when(deviceDataWindowRepository.countExceededInWindow(eq(DEVICE_UUID), anyLong()))
                    .thenReturn(1L);
            when(alertSuppressRepository.trySuppress(eq(DEVICE_UUID), eq(RULE_UUID), any(Duration.class)))
                    .thenReturn(true);

            alertEngineService.evaluate(dataPoint);

            verify(deviceDataWindowRepository).addDataPoint(eq(DEVICE_UUID), eq(2.0), anyLong());
            verify(alertRepository).save(any(Alert.class));
            verify(eventBus).publish(any());
        }
    }

    @Nested
    @DisplayName("evaluate — 抑制去重")
    class Suppression {

        @Test
        @DisplayName("抑制期内不重复创建报警")
        void shouldSuppressWithinCooldown() {
            when(deviceRepository.findById(DEVICE_UUID)).thenReturn(Optional.of(device));
            List<AlertRule> rules = new ArrayList<>();
            rules.add(rule);
            when(alertRuleRepository.findByDeviceUuid(DEVICE_UUID)).thenReturn(rules);
            when(alertRuleRepository.findAllGlobal()).thenReturn(List.of());
            when(deviceDataWindowRepository.countExceededInWindow(eq(DEVICE_UUID), anyLong()))
                    .thenReturn(1L);
            when(alertSuppressRepository.trySuppress(eq(DEVICE_UUID), eq(RULE_UUID), any(Duration.class)))
                    .thenReturn(false);

            alertEngineService.evaluate(dataPoint);

            verify(alertRepository, never()).save(any());
            verify(eventBus, never()).publish(any());
        }
    }

    @Nested
    @DisplayName("evaluate — 工单联动")
    class WorkOrderIntegration {

        @Test
        @DisplayName("规则启用 autoCreateWorkOrder 时自动创建工单")
        void shouldAutoCreateWorkOrderWhenEnabled() {
            AlertRule autoRule = new AlertRule(RULE_UUID, "自动工单规则", DEVICE_UUID,
                    AlertRuleType.THRESHOLD, "CH4", new BigDecimal("1.0"), 10,
                    AlertSeverity.WARNING, true, true, LocalDateTime.now(), LocalDateTime.now());

            when(deviceRepository.findById(DEVICE_UUID)).thenReturn(Optional.of(device));
            List<AlertRule> rules = new ArrayList<>();
            rules.add(autoRule);
            when(alertRuleRepository.findByDeviceUuid(DEVICE_UUID)).thenReturn(rules);
            when(alertRuleRepository.findAllGlobal()).thenReturn(List.of());
            when(deviceDataWindowRepository.countExceededInWindow(eq(DEVICE_UUID), anyLong()))
                    .thenReturn(1L);
            when(alertSuppressRepository.trySuppress(eq(DEVICE_UUID), eq(RULE_UUID), any(Duration.class)))
                    .thenReturn(true);

            WorkOrderVO mockWO = new WorkOrderVO();
            mockWO.setWorkOrderUuid("wo-001");
            when(workOrderService.createWorkOrder(any(CreateWorkOrderDTO.class)))
                    .thenReturn(mockWO);

            alertEngineService.evaluate(dataPoint);

            verify(workOrderService).createWorkOrder(any(CreateWorkOrderDTO.class));
        }

        @Test
        @DisplayName("工单创建失败时不阻断报警流程")
        void shouldNotFailAlertWhenWorkOrderFails() {
            AlertRule autoRule = new AlertRule(RULE_UUID, "自动工单规则", DEVICE_UUID,
                    AlertRuleType.THRESHOLD, "CH4", new BigDecimal("1.0"), 10,
                    AlertSeverity.WARNING, true, true, LocalDateTime.now(), LocalDateTime.now());

            when(deviceRepository.findById(DEVICE_UUID)).thenReturn(Optional.of(device));
            List<AlertRule> rules = new ArrayList<>();
            rules.add(autoRule);
            when(alertRuleRepository.findByDeviceUuid(DEVICE_UUID)).thenReturn(rules);
            when(alertRuleRepository.findAllGlobal()).thenReturn(List.of());
            when(deviceDataWindowRepository.countExceededInWindow(eq(DEVICE_UUID), anyLong()))
                    .thenReturn(1L);
            when(alertSuppressRepository.trySuppress(eq(DEVICE_UUID), eq(RULE_UUID), any(Duration.class)))
                    .thenReturn(true);
            when(workOrderService.createWorkOrder(any())).thenThrow(new RuntimeException("DB error"));

            alertEngineService.evaluate(dataPoint);

            verify(alertRepository, times(1)).save(any(Alert.class));
            verify(eventBus).publish(any());
        }
    }
}
