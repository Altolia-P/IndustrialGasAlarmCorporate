package com.niit.industrialgasalarmcorporate.application.notification.service.impl;

import com.niit.industrialgasalarmcorporate.application.notification.vo.NotificationVO;
import com.niit.industrialgasalarmcorporate.common.base.Page;
import com.niit.industrialgasalarmcorporate.common.exception.BusinessException;
import com.niit.industrialgasalarmcorporate.domain.event.AlertCreatedEvent;
import com.niit.industrialgasalarmcorporate.domain.notification.Notification;
import com.niit.industrialgasalarmcorporate.domain.notification.NotificationChannel;
import com.niit.industrialgasalarmcorporate.domain.notification.NotificationRepository;
import com.niit.industrialgasalarmcorporate.domain.notification.NotificationSender;
import com.niit.industrialgasalarmcorporate.domain.notification.NotificationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("NotificationServiceImpl — 通知触达")
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationSender smsSender;

    @Mock
    private NotificationSender emailSender;

    @Mock
    private org.springframework.transaction.support.TransactionTemplate transactionTemplate;

    private NotificationServiceImpl notificationService;

    private AlertCreatedEvent event;

    private static final String ALERT_UUID = "alert-001";

    @BeforeEach
    void setUp() {
        when(smsSender.supportedChannel()).thenReturn(NotificationChannel.SMS);
        when(emailSender.supportedChannel()).thenReturn(NotificationChannel.EMAIL);

        // 让 TransactionTemplate mock 真正执行回调，确保站内通知保存能被测试断言到
        doAnswer(invocation -> {
            java.util.function.Consumer<?> action = invocation.getArgument(0);
            action.accept(null);
            return null;
        }).when(transactionTemplate).executeWithoutResult(any());

        List<NotificationSender> senders = new ArrayList<>();
        senders.add(smsSender);
        senders.add(emailSender);

        notificationService = new NotificationServiceImpl(notificationRepository, senders, transactionTemplate);

        event = new AlertCreatedEvent(ALERT_UUID, "device-001", "THRESHOLD",
                "WARNING", "设备 device-001 (CH4) 气体浓度 2.0000 超过阈值 1.0000");
    }

    @Nested
    @DisplayName("notifyAlert — 双通道分流")
    class NotifyAlertDistribution {

        @Test
        @DisplayName("发送站内通知 + SMS + EMAIL 三个通道")
        void shouldSendToAllChannels() {
            when(smsSender.send(any())).thenReturn(true);
            when(emailSender.send(any())).thenReturn(true);

            notificationService.notifyAlert(event);

            // 站内通知保存 1 次 + SMS 保存 1 次 + EMAIL 保存 1 次 = 3 次
            verify(notificationRepository, times(3)).save(any(Notification.class));

            // 验证两个外部 Sender 均被调用
            verify(smsSender).send(any(Notification.class));
            verify(emailSender).send(any(Notification.class));
        }

        @Test
        @DisplayName("SMS 发送失败时降级，EMAIL 仍正常发送")
        void shouldDegradeWhenSmsFails() {
            when(smsSender.send(any())).thenReturn(false);
            when(emailSender.send(any())).thenReturn(true);

            notificationService.notifyAlert(event);

            // 3 次 save: 站内 + SMS(失败) + EMAIL(成功)
            verify(notificationRepository, times(3)).save(any(Notification.class));
            verify(smsSender).send(any(Notification.class));
            verify(emailSender).send(any(Notification.class));
        }

        @Test
        @DisplayName("SMS 发送抛异常时降级并持久化 FAILED 记录，EMAIL 仍正常发送")
        void shouldDegradeWhenSmsThrows() {
            when(smsSender.send(any())).thenThrow(new RuntimeException("SMS 服务超时"));
            when(emailSender.send(any())).thenReturn(true);

            notificationService.notifyAlert(event);

            // 3 次 save: 站内(SENT) + SMS(FAILED) + EMAIL(SENT)
            verify(notificationRepository, times(3)).save(any(Notification.class));
            verify(smsSender).send(any(Notification.class));
            verify(emailSender).send(any(Notification.class));
        }

        @Test
        @DisplayName("EMAIL 发送失败时降级，SMS 仍正常发送")
        void shouldDegradeWhenEmailFails() {
            when(smsSender.send(any())).thenReturn(true);
            when(emailSender.send(any())).thenReturn(false);

            notificationService.notifyAlert(event);

            verify(notificationRepository, times(3)).save(any(Notification.class));
            verify(smsSender).send(any(Notification.class));
            verify(emailSender).send(any(Notification.class));
        }

        @Test
        @DisplayName("EMAIL 发送抛异常时降级并持久化 FAILED 记录，SMS 仍正常发送")
        void shouldDegradeWhenEmailThrows() {
            when(smsSender.send(any())).thenReturn(true);
            when(emailSender.send(any())).thenThrow(new RuntimeException("邮件服务不可达"));

            notificationService.notifyAlert(event);

            // 3 次 save: 站内(SENT) + SMS(SENT) + EMAIL(FAILED)
            verify(notificationRepository, times(3)).save(any(Notification.class));
            verify(smsSender).send(any(Notification.class));
            verify(emailSender).send(any(Notification.class));
        }

        @Test
        @DisplayName("两个外部通道均失败时仍不抛出异常")
        void shouldNotThrowWhenBothChannelsFail() {
            when(smsSender.send(any())).thenReturn(false);
            when(emailSender.send(any())).thenReturn(false);

            assertDoesNotThrow(() -> notificationService.notifyAlert(event));

            verify(notificationRepository, times(3)).save(any(Notification.class));
        }

        @Test
        @DisplayName("两个外部通道均抛异常时仍不抛出异常，3条记录全部持久化")
        void shouldNotThrowWhenBothChannelsThrow() {
            when(smsSender.send(any())).thenThrow(new RuntimeException("SMS 失败"));
            when(emailSender.send(any())).thenThrow(new RuntimeException("EMAIL 失败"));

            assertDoesNotThrow(() -> notificationService.notifyAlert(event));

            // 3 次 save: 站内(SENT) + SMS(FAILED) + EMAIL(FAILED)
            verify(notificationRepository, times(3)).save(any(Notification.class));
        }

        @Test
        @DisplayName("站内通知记录状态为 SENT")
        void shouldMarkInAppAsSent() {
            when(smsSender.send(any())).thenReturn(true);
            when(emailSender.send(any())).thenReturn(true);

            notificationService.notifyAlert(event);

            ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
            verify(notificationRepository, times(3)).save(captor.capture());

            // 第一个 save 是站内通知
            Notification inApp = captor.getAllValues().get(0);
            assertEquals(NotificationChannel.IN_APP, inApp.getChannel());
            assertEquals(NotificationStatus.SENT, inApp.getStatus());
        }

        @Test
        @DisplayName("SMS 发送成功时记录 SENT 状态")
        void shouldMarkSmsAsSentOnSuccess() {
            when(smsSender.send(any())).thenReturn(true);
            when(emailSender.send(any())).thenReturn(true);

            notificationService.notifyAlert(event);

            ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
            verify(notificationRepository, times(3)).save(captor.capture());

            Notification sms = captor.getAllValues().get(1); // 第二个是 SMS
            assertEquals(NotificationChannel.SMS, sms.getChannel());
            assertEquals(NotificationStatus.SENT, sms.getStatus());
        }

        @Test
        @DisplayName("SMS 发送失败时记录 FAILED 状态及错误信息")
        void shouldMarkSmsAsFailedOnReturnFalse() {
            when(smsSender.send(any())).thenReturn(false);
            when(emailSender.send(any())).thenReturn(true);

            notificationService.notifyAlert(event);

            ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
            verify(notificationRepository, times(3)).save(captor.capture());

            Notification sms = captor.getAllValues().get(1);
            assertEquals(NotificationChannel.SMS, sms.getChannel());
            assertEquals(NotificationStatus.FAILED, sms.getStatus());
            assertNotNull(sms.getErrorMessage());
        }

        @Test
        @DisplayName("SMS 抛异常时记录 FAILED 状态及异常信息")
        void shouldMarkSmsAsFailedOnException() {
            when(smsSender.send(any())).thenThrow(new RuntimeException("SMS 服务超时"));
            when(emailSender.send(any())).thenReturn(true);

            notificationService.notifyAlert(event);

            ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
            verify(notificationRepository, times(3)).save(captor.capture());

            Notification sms = captor.getAllValues().get(1);
            assertEquals(NotificationChannel.SMS, sms.getChannel());
            assertEquals(NotificationStatus.FAILED, sms.getStatus());
            assertNotNull(sms.getErrorMessage());
            assertTrue(sms.getErrorMessage().contains("SMS 服务超时"));
        }

        @Test
        @DisplayName("IN_APP 类型的 Sender 被自动跳过（不重复发站内通知）")
        void shouldSkipInAppSender() {
            NotificationSender inAppSender = mock(NotificationSender.class);
            when(inAppSender.supportedChannel()).thenReturn(NotificationChannel.IN_APP);

            // 重新构建 service，包含 IN_APP Sender
            List<NotificationSender> sendersWithInApp = new ArrayList<>();
            sendersWithInApp.add(inAppSender);
            sendersWithInApp.add(smsSender);
            sendersWithInApp.add(emailSender);
            notificationService = new NotificationServiceImpl(notificationRepository, sendersWithInApp, transactionTemplate);

            when(smsSender.send(any())).thenReturn(true);
            when(emailSender.send(any())).thenReturn(true);

            notificationService.notifyAlert(event);

            // IN_APP Sender 不应被调用
            verify(inAppSender, never()).send(any());
            // 但 sms 和 email 正常
            verify(smsSender).send(any());
            verify(emailSender).send(any());
        }

        @Test
        @DisplayName("未注册外部 Sender 时仅发送站内通知")
        void shouldOnlySendInAppWhenNoExternalSenders() {
            // 清空 senders 列表
            notificationService = new NotificationServiceImpl(notificationRepository, List.of(), transactionTemplate);

            notificationService.notifyAlert(event);

            verify(notificationRepository, times(1)).save(any(Notification.class));
            verify(smsSender, never()).send(any());
            verify(emailSender, never()).send(any());
        }

        @Test
        @DisplayName("站内通知内容包含完整报警信息")
        void shouldBuildContentWithAlertInfo() {
            when(smsSender.send(any())).thenReturn(true);
            when(emailSender.send(any())).thenReturn(true);

            notificationService.notifyAlert(event);

            ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
            verify(notificationRepository, times(3)).save(captor.capture());

            Notification inApp = captor.getAllValues().get(0);
            assertTrue(inApp.getContent().contains("WARNING"));
            assertTrue(inApp.getContent().contains("device-001"));
            assertTrue(inApp.getContent().contains("2.0000"));
        }

        @Test
        @DisplayName("外部渠道通知内容已脱敏，不包含详细消息体（PII 防护）")
        void shouldSanitizeExternalContentForPii() {
            when(smsSender.send(any())).thenReturn(true);
            when(emailSender.send(any())).thenReturn(true);

            notificationService.notifyAlert(event);

            ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
            verify(notificationRepository, times(3)).save(captor.capture());

            // SMS (index 1) 和 EMAIL (index 2) 的内容不应包含原始消息体
            Notification sms = captor.getAllValues().get(1);
            Notification email = captor.getAllValues().get(2);

            // 外部渠道应包含严重级别、报警类型和设备标识
            assertTrue(sms.getContent().contains("WARNING"));
            assertTrue(sms.getContent().contains("device-001"));

            // 外部渠道不应包含详细的消息体（含浓度数值）
            assertFalse(sms.getContent().contains("2.0000"));
            assertFalse(sms.getContent().contains("CH4"));
            assertFalse(sms.getContent().contains("阈值"));
            assertFalse(email.getContent().contains("2.0000"));
            assertFalse(email.getContent().contains("CH4"));

            // 外部渠道应提示用户到平台查看详情
            assertTrue(sms.getContent().contains("登录平台查看详情"));
        }
    }

    @Nested
    @DisplayName("findByAlertUuid — 按报警查询通知")
    class FindByAlertUuid {

        @SuppressWarnings("unchecked")
        @Test
        @DisplayName("返回分页通知列表")
        void shouldReturnPagedNotifications() {
            List<Notification> mockList = List.of(
                    new Notification(ALERT_UUID, "system", NotificationChannel.IN_APP, "content-1"),
                    new Notification(ALERT_UUID, "sms-placeholder", NotificationChannel.SMS, "content-2")
            );
            Page<Notification> domainPage = new Page<>(mockList, 2, 10, 1);
            when(notificationRepository.findByAlertUuid(ALERT_UUID, 1, 10)).thenReturn(domainPage);

            Page<NotificationVO> result = notificationService.findByAlertUuid(ALERT_UUID, 1, 10);

            assertEquals(2, result.getTotalElements());
            assertEquals(2, result.getContent().size());
            assertEquals("IN_APP", result.getContent().get(0).getChannel());
            assertEquals("SMS", result.getContent().get(1).getChannel());
        }

        @SuppressWarnings("unchecked")
        @Test
        @DisplayName("空列表时返回空分页")
        void shouldReturnEmptyPageWhenNoNotifications() {
            Page<Notification> emptyPage = new Page<>(List.of(), 0, 10, 1);
            when(notificationRepository.findByAlertUuid(ALERT_UUID, 1, 10)).thenReturn(emptyPage);

            Page<NotificationVO> result = notificationService.findByAlertUuid(ALERT_UUID, 1, 10);

            assertEquals(0, result.getTotalElements());
            assertTrue(result.getContent().isEmpty());
        }
    }

    @Nested
    @DisplayName("listAll — 全部通知列表")
    class ListAll {

        @SuppressWarnings("unchecked")
        @Test
        @DisplayName("返回全部分页通知")
        void shouldReturnAllNotifications() {
            List<Notification> mockList = List.of(
                    new Notification("alert-003", "system", NotificationChannel.IN_APP, "content")
            );
            Page<Notification> domainPage = new Page<>(mockList, 1, 20, 1);
            when(notificationRepository.findAll(1, 20)).thenReturn(domainPage);

            Page<NotificationVO> result = notificationService.listAll(1, 20);

            assertEquals(1, result.getTotalElements());
            assertEquals(1, result.getContent().size());
        }
    }

    @Nested
    @DisplayName("resend — 重发通知")
    class Resend {

        @Test
        @DisplayName("IN_APP 通知不存在匹配 Sender，仅更新状态为 SENT")
        void shouldMarkInAppAsSentWhenNoSender() {
            Notification notification = new Notification(ALERT_UUID, "system",
                    NotificationChannel.IN_APP, "content");
            when(notificationRepository.findById("notif-001")).thenReturn(Optional.of(notification));

            notificationService.resend("notif-001");

            verify(notificationRepository).save(any(Notification.class));
            assertEquals(NotificationStatus.SENT, notification.getStatus());
        }

        @Test
        @DisplayName("SMS 通知重发时调用 SMS Sender 并更新为 SENT")
        void shouldSendViaSmsSenderOnResend() {
            Notification notification = new Notification(ALERT_UUID, "13800138000",
                    NotificationChannel.SMS, "content");
            when(notificationRepository.findById("notif-002")).thenReturn(Optional.of(notification));
            when(smsSender.send(any())).thenReturn(true);

            notificationService.resend("notif-002");

            verify(smsSender).send(notification);
            assertEquals(NotificationStatus.SENT, notification.getStatus());
            verify(notificationRepository).save(notification);
        }

        @Test
        @DisplayName("EMAIL 通知重发时调用 Email Sender 并更新为 SENT")
        void shouldSendViaEmailSenderOnResend() {
            Notification notification = new Notification(ALERT_UUID, "user@example.com",
                    NotificationChannel.EMAIL, "content");
            when(notificationRepository.findById("notif-003")).thenReturn(Optional.of(notification));
            when(emailSender.send(any())).thenReturn(true);

            notificationService.resend("notif-003");

            verify(emailSender).send(notification);
            assertEquals(NotificationStatus.SENT, notification.getStatus());
            verify(notificationRepository).save(notification);
        }

        @Test
        @DisplayName("SMS 重发失败时标记 FAILED")
        void shouldMarkFailedWhenResendFails() {
            Notification notification = new Notification(ALERT_UUID, "13800138000",
                    NotificationChannel.SMS, "content");
            when(notificationRepository.findById("notif-004")).thenReturn(Optional.of(notification));
            when(smsSender.send(any())).thenReturn(false);

            notificationService.resend("notif-004");

            verify(smsSender).send(notification);
            assertEquals(NotificationStatus.FAILED, notification.getStatus());
            assertNotNull(notification.getErrorMessage());
            verify(notificationRepository).save(notification);
        }

        @Test
        @DisplayName("SMS 重发抛异常时标记 FAILED 并记录异常信息")
        void shouldMarkFailedWhenResendThrows() {
            Notification notification = new Notification(ALERT_UUID, "13800138000",
                    NotificationChannel.SMS, "content");
            when(notificationRepository.findById("notif-005")).thenReturn(Optional.of(notification));
            when(smsSender.send(any())).thenThrow(new RuntimeException("短信服务不可用"));

            notificationService.resend("notif-005");

            verify(smsSender).send(notification);
            assertEquals(NotificationStatus.FAILED, notification.getStatus());
            assertTrue(notification.getErrorMessage().contains("短信服务不可用"));
            verify(notificationRepository).save(notification);
        }

        @Test
        @DisplayName("通知不存在时抛出 BusinessException")
        void shouldThrowWhenNotificationNotFound() {
            when(notificationRepository.findById("notif-unknown")).thenReturn(Optional.empty());

            assertThrows(BusinessException.class,
                    () -> notificationService.resend("notif-unknown"));
            verify(notificationRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("getUnreadCount — 未读数统计")
    class GetUnreadCount {

        @Test
        @DisplayName("返回指定时间后的未读数")
        void shouldReturnCountSinceGivenTime() {
            LocalDateTime since = LocalDateTime.now().minusHours(1);
            when(notificationRepository.countByChannelAndCreatedAfter(NotificationChannel.IN_APP, since))
                    .thenReturn(5L);

            long count = notificationService.getUnreadCount(since);

            assertEquals(5L, count);
        }
    }

    @Nested
    @DisplayName("getRecent — 最近通知")
    class GetRecent {

        @Test
        @DisplayName("返回指定数量的最近通知")
        void shouldReturnRecentNotifications() {
            List<Notification> mockList = List.of(
                    new Notification(ALERT_UUID, "system", NotificationChannel.IN_APP, "recent-1"),
                    new Notification(ALERT_UUID, "system", NotificationChannel.IN_APP, "recent-2")
            );
            when(notificationRepository.findRecentByChannel(NotificationChannel.IN_APP, 5))
                    .thenReturn(mockList);

            List<NotificationVO> result = notificationService.getRecent(5);

            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("无通知时返回空列表")
        void shouldReturnEmptyListWhenNoRecent() {
            when(notificationRepository.findRecentByChannel(NotificationChannel.IN_APP, 5))
                    .thenReturn(List.of());

            List<NotificationVO> result = notificationService.getRecent(5);

            assertTrue(result.isEmpty());
        }
    }
}
