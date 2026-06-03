package com.niit.industrialgasalarmcorporate.application.alert.service.impl;

import com.niit.industrialgasalarmcorporate.common.enums.ErrorCode;
import com.niit.industrialgasalarmcorporate.common.exception.BusinessException;
import com.niit.industrialgasalarmcorporate.domain.alert.Alert;
import com.niit.industrialgasalarmcorporate.domain.alert.AlertRepository;
import com.niit.industrialgasalarmcorporate.domain.alert.AlertRuleType;
import com.niit.industrialgasalarmcorporate.domain.alert.AlertSeverity;
import com.niit.industrialgasalarmcorporate.domain.alert.AlertStatus;
import com.niit.industrialgasalarmcorporate.infrastructure.redis.DashboardCacheRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AlertServiceImpl 告警服务")
class AlertServiceImplTest {

    @Mock private AlertRepository alertRepository;
    @Mock private com.niit.industrialgasalarmcorporate.domain.device.DeviceRepository deviceRepository;
    @Mock private com.niit.industrialgasalarmcorporate.domain.auth.UserRepository userRepository;
    @Mock private DashboardCacheRepository dashboardCacheRepository;

    @InjectMocks
    private AlertServiceImpl alertService;

    private Alert alert;
    private static final String ALERT_UUID = "alert-001";
    private static final String USERNAME = "admin01";

    @BeforeEach
    void setUp() {
        alert = new Alert(
                ALERT_UUID,
                "device-001",
                "rule-001",
                AlertRuleType.THRESHOLD,
                AlertSeverity.CRITICAL,
                new BigDecimal("2.0"),
                new BigDecimal("1.0"),
                "测试告警消息",
                AlertStatus.PENDING,
                LocalDateTime.now(),
                null, null, null, null,
                null,
                LocalDateTime.now(), LocalDateTime.now(), 0
        );
    }

    @Nested
    @DisplayName("confirmAlert — 告警确认")
    class ConfirmAlert {

        @Test
        @DisplayName("PENDING 告警确认成功，状态变为 CONFIRMED")
        void shouldConfirmPendingAlert() {
            when(alertRepository.findById(ALERT_UUID)).thenReturn(Optional.of(alert));

            alertService.confirmAlert(ALERT_UUID, USERNAME);

            assertEquals(AlertStatus.CONFIRMED, alert.getStatus());
            assertEquals(USERNAME, alert.getConfirmedBy());
            assertNotNull(alert.getConfirmedAt());
            verify(alertRepository).save(alert);
            verify(dashboardCacheRepository).evict("dashboard:stats");
        }

        @Test
        @DisplayName("告警不存在时抛出 ALERT_NOT_FOUND")
        void shouldThrowWhenAlertNotFound() {
            when(alertRepository.findById(ALERT_UUID)).thenReturn(Optional.empty());

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> alertService.confirmAlert(ALERT_UUID, USERNAME));
            assertEquals(ErrorCode.ALERT_NOT_FOUND.getCode(), ex.getCode());
            verify(alertRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("resolveAlert — 告警解决")
    class ResolveAlert {

        @Test
        @DisplayName("告警成功解决，状态变为 RESOLVED")
        void shouldResolveAlert() {
            alert.confirm(USERNAME);
            when(alertRepository.findById(ALERT_UUID)).thenReturn(Optional.of(alert));

            alertService.resolveAlert(ALERT_UUID, USERNAME);

            assertEquals(AlertStatus.RESOLVED, alert.getStatus());
            assertEquals(USERNAME, alert.getResolvedBy());
            assertNotNull(alert.getResolvedAt());
            verify(alertRepository).save(alert);
            verify(dashboardCacheRepository).evict("dashboard:stats");
        }
    }

    @Nested
    @DisplayName("closeAlert — 告警关闭")
    class CloseAlert {

        @Test
        @DisplayName("告警成功关闭，状态变为 CLOSED")
        void shouldCloseAlert() {
            when(alertRepository.findById(ALERT_UUID)).thenReturn(Optional.of(alert));

            alertService.closeAlert(ALERT_UUID);

            assertEquals(AlertStatus.CLOSED, alert.getStatus());
            verify(alertRepository).save(alert);
            verify(dashboardCacheRepository).evict("dashboard:stats");
        }
    }
}
