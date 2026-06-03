package com.niit.industrialgasalarmcorporate.application.notification.service.impl;

import com.niit.industrialgasalarmcorporate.application.notification.service.NotificationService;
import com.niit.industrialgasalarmcorporate.application.notification.vo.NotificationVO;
import com.niit.industrialgasalarmcorporate.assembler.NotificationAssembler;
import com.niit.industrialgasalarmcorporate.common.base.Page;
import com.niit.industrialgasalarmcorporate.common.enums.ErrorCode;
import com.niit.industrialgasalarmcorporate.common.exception.BusinessException;
import com.niit.industrialgasalarmcorporate.domain.event.AlertCreatedEvent;
import com.niit.industrialgasalarmcorporate.domain.notification.Notification;
import com.niit.industrialgasalarmcorporate.domain.notification.NotificationChannel;
import com.niit.industrialgasalarmcorporate.domain.notification.NotificationRepository;
import com.niit.industrialgasalarmcorporate.domain.notification.NotificationSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final List<NotificationSender> notificationSenders;
    private final TransactionTemplate transactionTemplate;

    @Override
    public void notifyAlert(AlertCreatedEvent event) {
        // 1. 站内通知 — 独立事务，外部渠道失败不影响站内通知的持久化
        transactionTemplate.executeWithoutResult(status -> {
            Notification notification = new Notification(
                    event.getAlertUuid(),
                    "system",
                    NotificationChannel.IN_APP,
                    buildContent(event)
            );
            notification.markSent();
            notificationRepository.save(notification);
            log.info("站内通知已发送: notificationUuid={}, alertUuid={}",
                    notification.getNotificationUuid(), event.getAlertUuid());
        });

        // 2. 外部渠道分发 (SMS / EMAIL 双通道)
        sendViaExternalChannels(event);
    }

    /**
     * 双通道分流：遍历所有注册的 NotificationSender，逐通道发送。
     * 单个 Sender 失败不阻断其它通道 — 降级语义。
     */
    private void sendViaExternalChannels(AlertCreatedEvent event) {
        for (NotificationSender sender : notificationSenders) {
            if (sender.supportedChannel() == NotificationChannel.IN_APP) {
                continue; // 站内通知已在事务中处理
            }
            Notification externalNotification = new Notification(
                    event.getAlertUuid(),
                    resolveRecipient(sender.supportedChannel()),
                    sender.supportedChannel(),
                    buildExternalContent(event)
            );
            try {
                boolean success = sender.send(externalNotification);
                if (success) {
                    externalNotification.markSent();
                    log.info("{} 通知发送成功: alertUuid={}",
                            sender.supportedChannel(), event.getAlertUuid());
                } else {
                    externalNotification.markFailed(
                            sender.supportedChannel() + " 发送返回失败");
                    log.warn("{} 通知发送失败(返回false): alertUuid={}",
                            sender.supportedChannel(), event.getAlertUuid());
                }
            } catch (Exception e) {
                externalNotification.markFailed(
                        sender.supportedChannel() + " 发送异常: " + e.getMessage());
                log.warn("{} 通知发送异常(降级): alertUuid={}",
                        sender.supportedChannel(), event.getAlertUuid(), e);
            }
            notificationRepository.save(externalNotification);
        }
    }

    /**
     * 根据渠道解析接收方地址（预留扩展 — 当前返回占位符）
     */
    private String resolveRecipient(NotificationChannel channel) {
        switch (channel) {
            case SMS:
                return "sms-placeholder";
            case EMAIL:
                return "email-placeholder";
            default:
                return "system";
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationVO> findByAlertUuid(String alertUuid, int page, int size) {
        Page<Notification> domainPage = notificationRepository.findByAlertUuid(alertUuid, page, size);
        return new Page<>(
                domainPage.getContent().stream().map(NotificationAssembler::toVO)
                        .collect(Collectors.toList()),
                domainPage.getTotalElements(),
                domainPage.getSize(),
                domainPage.getNumber()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationVO> listAll(int page, int size) {
        Page<Notification> domainPage = notificationRepository.findAll(page, size);
        return new Page<>(
                domainPage.getContent().stream().map(NotificationAssembler::toVO)
                        .collect(Collectors.toList()),
                domainPage.getTotalElements(),
                domainPage.getSize(),
                domainPage.getNumber()
        );
    }

    @Override
    @Transactional
    public void delete(String notificationUuid) {
        if (notificationRepository.findById(notificationUuid).isEmpty()) {
            throw new BusinessException(ErrorCode.NOTIFICATION_NOT_FOUND);
        }
        notificationRepository.deleteById(notificationUuid);
    }

    @Override
    @Transactional
    public void resend(String notificationUuid) {
        Notification notification = notificationRepository.findById(notificationUuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOTIFICATION_NOT_FOUND));
        // 重置重试状态
        notification.resetForRetry();
        // 查找对应的发送器
        NotificationSender sender = findSender(notification.getChannel());
        if (sender != null) {
            try {
                boolean success = sender.send(notification);
                if (success) {
                    notification.markSent();
                    log.info("通知重发成功: notificationUuid={}, channel={}",
                            notificationUuid, notification.getChannel());
                } else {
                    notification.markFailed(notification.getChannel() + " 重发返回失败");
                    log.warn("通知重发失败(返回false): notificationUuid={}, channel={}",
                            notificationUuid, notification.getChannel());
                }
            } catch (Exception e) {
                notification.markFailed("重发异常: " + e.getMessage());
                log.warn("通知重发异常: notificationUuid={}, channel={}",
                        notificationUuid, notification.getChannel(), e);
            }
        } else {
            // 找不到匹配的 Sender (如 IN_APP)，直接标记 SENT
            notification.markSent();
            log.info("通知重发(无匹配Sender): notificationUuid={}, channel={}",
                    notificationUuid, notification.getChannel());
        }
        notificationRepository.save(notification);
    }

    /**
     * 根据渠道查找注册的 NotificationSender
     */
    private NotificationSender findSender(NotificationChannel channel) {
        for (NotificationSender sender : notificationSenders) {
            if (sender.supportedChannel() == channel) {
                return sender;
            }
        }
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(java.time.LocalDateTime since) {
        return notificationRepository.countByChannelAndCreatedAfter(NotificationChannel.IN_APP, since);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationVO> getRecent(int limit) {
        return notificationRepository.findRecentByChannel(NotificationChannel.IN_APP, limit).stream()
                .map(NotificationAssembler::toVO)
                .collect(Collectors.toList());
    }

    /**
     * 站内通知内容：包含完整报警信息
     */
    private String buildContent(AlertCreatedEvent event) {
        return String.format("[%s][%s] 设备 %s 触发报警：%s",
                event.getSeverity(), event.getAlertType(),
                event.getDeviceUuid(), event.getMessage());
    }

    /**
     * 外部渠道（SMS/EMAIL）通知内容：脱敏处理，不包含详细消息体（PII 防护）
     */
    private String buildExternalContent(AlertCreatedEvent event) {
        return String.format("[%s][%s] 设备 %s 触发报警，请登录平台查看详情。",
                event.getSeverity(), event.getAlertType(),
                event.getDeviceUuid());
    }
}
