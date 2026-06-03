package com.niit.industrialgasalarmcorporate.application.alert.service.impl;

import com.niit.industrialgasalarmcorporate.domain.alert.*;
import com.niit.industrialgasalarmcorporate.domain.device.Device;
import com.niit.industrialgasalarmcorporate.domain.device.DeviceRepository;
import com.niit.industrialgasalarmcorporate.domain.device.DeviceStatus;
import com.niit.industrialgasalarmcorporate.domain.device.GasType;
import com.niit.industrialgasalarmcorporate.infrastructure.config.RabbitMQConfig;
import com.niit.industrialgasalarmcorporate.infrastructure.mq.AlertMessage;
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
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AlertEngineService 报警引擎")
class AlertEngineServiceTest {

    @Mock private DeviceRepository deviceRepository;
    @Mock private AlertRuleRepository alertRuleRepository;
    @Mock private DeviceDataWindowRepository deviceDataWindowRepository;
    @Mock private AlertSuppressRepository alertSuppressRepository;
    @Mock private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private AlertEngineServiceImpl alertEngineService;

    private Device device;
    private AlertRule rule;
    private static final String DEVICE_UUID = "device-001";
    private static final String RULE_UUID = "rule-001";
    private static final BigDecimal CONCENTRATION = new BigDecimal("2.0");
    private static final LocalDateTime TIMESTAMP = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        device = new Device(DEVICE_UUID, "SN-001", "test-api-token", "测试设备", "GT-M4", "cust-001",
                "A区", null, GasType.CH4, new BigDecimal("0"), new BigDecimal("5.0"),
                new BigDecimal("1.0"), DeviceStatus.NORMAL,
                LocalDateTime.now(), LocalDateTime.now());

        rule = new AlertRule(RULE_UUID, "阈值规则", DEVICE_UUID, AlertRuleType.THRESHOLD,
                "CH4", new BigDecimal("1.0"), 10, AlertSeverity.WARNING,
                false, true, LocalDateTime.now(), LocalDateTime.now());
    }

    @Nested
    @DisplayName("evaluate — 阈值判断")
    class ThresholdEvaluation {

        @Test
        @DisplayName("设备不存在时静默返回")
        void shouldReturnSilentlyWhenDeviceNotFound() {
            when(deviceRepository.findById(DEVICE_UUID)).thenReturn(Optional.empty());

            alertEngineService.evaluate(DEVICE_UUID, CONCENTRATION, TIMESTAMP);

            verify(alertRuleRepository, never()).findByDeviceUuid(any());
        }

        @Test
        @DisplayName("规则不匹配时跳过评估")
        void shouldSkipWhenRuleDoesNotMatch() {
            Device h2sDevice = new Device(DEVICE_UUID, "SN-002", "test-api-token", "H2S设备", "GT-H2S", "cust-001",
                    "B区", null, GasType.H2S, new BigDecimal("0"), new BigDecimal("5.0"),
                    new BigDecimal("1.0"), DeviceStatus.NORMAL,
                    LocalDateTime.now(), LocalDateTime.now());
            when(deviceRepository.findById(DEVICE_UUID)).thenReturn(Optional.of(h2sDevice));
            List<AlertRule> rules = new ArrayList<>();
            rules.add(rule);
            when(alertRuleRepository.findByDeviceUuid(DEVICE_UUID)).thenReturn(rules);
            when(alertRuleRepository.findAllGlobal()).thenReturn(List.of());

            alertEngineService.evaluate(DEVICE_UUID, CONCENTRATION, TIMESTAMP);

            verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), any(AlertMessage.class));
        }

        @Test
        @DisplayName("浓度未超过阈值时不触发报警")
        void shouldNotTriggerWhenBelowThreshold() {
            when(deviceRepository.findById(DEVICE_UUID)).thenReturn(Optional.of(device));
            List<AlertRule> rules = new ArrayList<>();
            rules.add(rule);
            when(alertRuleRepository.findByDeviceUuid(DEVICE_UUID)).thenReturn(rules);
            when(alertRuleRepository.findAllGlobal()).thenReturn(List.of());

            alertEngineService.evaluate(DEVICE_UUID, new BigDecimal("0.5"), TIMESTAMP);

            verify(deviceDataWindowRepository, never()).addDataPoint(any(), any(), anyDouble(), anyLong());
            verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), any(AlertMessage.class));
        }

        @Test
        @DisplayName("浓度超过阈值且窗口计数>=1 时发送告警消息到 RabbitMQ")
        void shouldSendAlertMessageWhenThresholdExceeded() {
            when(deviceRepository.findById(DEVICE_UUID)).thenReturn(Optional.of(device));
            List<AlertRule> rules = new ArrayList<>();
            rules.add(rule);
            when(alertRuleRepository.findByDeviceUuid(DEVICE_UUID)).thenReturn(rules);
            when(alertRuleRepository.findAllGlobal()).thenReturn(List.of());
            when(deviceDataWindowRepository.countExceededInWindow(eq(DEVICE_UUID), eq(RULE_UUID), anyLong()))
                    .thenReturn(1L);
            when(alertSuppressRepository.trySuppress(eq(DEVICE_UUID), eq(RULE_UUID), any(Duration.class)))
                    .thenReturn(true);

            alertEngineService.evaluate(DEVICE_UUID, CONCENTRATION, TIMESTAMP);

            verify(deviceDataWindowRepository).addDataPoint(eq(DEVICE_UUID), eq(RULE_UUID), eq(2.0), anyLong());
            verify(rabbitTemplate).convertAndSend(
                    eq(RabbitMQConfig.ALERT_EXCHANGE),
                    eq(RabbitMQConfig.ALERT_ROUTING_KEY),
                    any(AlertMessage.class));
        }
    }

    @Nested
    @DisplayName("evaluate — 抑制去重")
    class Suppression {

        @Test
        @DisplayName("抑制期内不发送告警消息")
        void shouldSuppressWithinCooldown() {
            when(deviceRepository.findById(DEVICE_UUID)).thenReturn(Optional.of(device));
            List<AlertRule> rules = new ArrayList<>();
            rules.add(rule);
            when(alertRuleRepository.findByDeviceUuid(DEVICE_UUID)).thenReturn(rules);
            when(alertRuleRepository.findAllGlobal()).thenReturn(List.of());
            when(alertSuppressRepository.trySuppress(eq(DEVICE_UUID), eq(RULE_UUID), any(Duration.class)))
                    .thenReturn(false);

            alertEngineService.evaluate(DEVICE_UUID, CONCENTRATION, TIMESTAMP);

            verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), any(AlertMessage.class));
        }
    }

    @Nested
    @DisplayName("evaluate — 告警消息内容")
    class AlertMessageContent {

        @Test
        @DisplayName("告警消息应包含设备名称和阈值信息")
        void shouldIncludeDeviceInfoInMessage() {
            when(deviceRepository.findById(DEVICE_UUID)).thenReturn(Optional.of(device));
            List<AlertRule> rules = new ArrayList<>();
            rules.add(rule);
            when(alertRuleRepository.findByDeviceUuid(DEVICE_UUID)).thenReturn(rules);
            when(alertRuleRepository.findAllGlobal()).thenReturn(List.of());
            when(deviceDataWindowRepository.countExceededInWindow(eq(DEVICE_UUID), eq(RULE_UUID), anyLong()))
                    .thenReturn(1L);
            when(alertSuppressRepository.trySuppress(eq(DEVICE_UUID), eq(RULE_UUID), any(Duration.class)))
                    .thenReturn(true);

            alertEngineService.evaluate(DEVICE_UUID, CONCENTRATION, TIMESTAMP);

            ArgumentCaptor<AlertMessage> captor = ArgumentCaptor.forClass(AlertMessage.class);
            verify(rabbitTemplate).convertAndSend(
                    eq(RabbitMQConfig.ALERT_EXCHANGE),
                    eq(RabbitMQConfig.ALERT_ROUTING_KEY),
                    captor.capture());
            AlertMessage msg = captor.getValue();
            assertEquals(DEVICE_UUID, msg.getDeviceUuid());
            assertEquals(RULE_UUID, msg.getRuleUuid());
            assertEquals("测试设备", msg.getDeviceName());
            assertEquals(CONCENTRATION, msg.getConcentration());
            assertEquals(rule.getThreshold(), msg.getThreshold());
            assertEquals(AlertRuleType.THRESHOLD.name(), msg.getAlertType());
        }
    }
}
