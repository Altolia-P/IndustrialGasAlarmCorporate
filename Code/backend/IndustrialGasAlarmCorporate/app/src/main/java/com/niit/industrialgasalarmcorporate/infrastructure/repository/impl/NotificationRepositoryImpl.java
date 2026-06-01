package com.niit.industrialgasalarmcorporate.infrastructure.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niit.industrialgasalarmcorporate.domain.notification.Notification;
import com.niit.industrialgasalarmcorporate.domain.notification.NotificationChannel;
import com.niit.industrialgasalarmcorporate.domain.notification.NotificationRepository;
import com.niit.industrialgasalarmcorporate.domain.notification.NotificationStatus;
import com.niit.industrialgasalarmcorporate.infrastructure.repository.mapper.NotificationMapper;
import com.niit.industrialgasalarmcorporate.infrastructure.repository.po.NotificationPO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepository {

    private final NotificationMapper notificationMapper;

    @Override
    public void save(Notification notification) {
        NotificationPO po = toPO(notification);
        NotificationPO existing = notificationMapper.selectById(notification.getNotificationUuid());
        if (existing != null) {
            notificationMapper.updateById(po);
        } else {
            notificationMapper.insert(po);
        }
    }

    @Override
    public Optional<Notification> findById(String notificationUuid) {
        NotificationPO po = notificationMapper.selectById(notificationUuid);
        if (po == null) {
            return Optional.empty();
        }
        return Optional.of(toDomain(po));
    }

    @Override
    public com.niit.industrialgasalarmcorporate.common.base.Page<Notification> findByAlertUuid(
            String alertUuid, int page, int size) {
        LambdaQueryWrapper<NotificationPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NotificationPO::getAlertUuid, alertUuid)
                .orderByDesc(NotificationPO::getCreatedAt);
        Page<NotificationPO> mpPage = new Page<>(page, size);
        Page<NotificationPO> result = notificationMapper.selectPage(mpPage, wrapper);
        List<Notification> notifications = result.getRecords().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
        return new com.niit.industrialgasalarmcorporate.common.base.Page<>(
                notifications, result.getTotal(), (int) result.getSize(), (int) result.getCurrent());
    }

    @Override
    public com.niit.industrialgasalarmcorporate.common.base.Page<Notification> findAll(int page, int size) {
        LambdaQueryWrapper<NotificationPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(NotificationPO::getCreatedAt);
        Page<NotificationPO> mpPage = new Page<>(page, size);
        Page<NotificationPO> result = notificationMapper.selectPage(mpPage, wrapper);
        List<Notification> notifications = result.getRecords().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
        return new com.niit.industrialgasalarmcorporate.common.base.Page<>(
                notifications, result.getTotal(), (int) result.getSize(), (int) result.getCurrent());
    }

    @Override
    public long countByChannelAndCreatedAfter(NotificationChannel channel, java.time.LocalDateTime since) {
        LambdaQueryWrapper<NotificationPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NotificationPO::getChannel, channel.name())
                .gt(NotificationPO::getCreatedAt, since);
        return notificationMapper.selectCount(wrapper);
    }

    @Override
    public List<Notification> findRecentByChannel(NotificationChannel channel, int limit) {
        LambdaQueryWrapper<NotificationPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NotificationPO::getChannel, channel.name())
                .orderByDesc(NotificationPO::getCreatedAt)
                .last("LIMIT " + limit);
        return notificationMapper.selectList(wrapper).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private Notification toDomain(NotificationPO po) {
        return new Notification(
                po.getNotificationUuid(),
                po.getAlertUuid(),
                po.getRecipient(),
                NotificationChannel.valueOf(po.getChannel()),
                po.getContent(),
                NotificationStatus.valueOf(po.getStatus()),
                po.getRetryCount(),
                po.getErrorMessage(),
                po.getSentAt(),
                po.getCreatedAt(),
                po.getUpdatedAt()
        );
    }

    private NotificationPO toPO(Notification notification) {
        NotificationPO po = new NotificationPO();
        po.setNotificationUuid(notification.getNotificationUuid());
        po.setAlertUuid(notification.getAlertUuid());
        po.setRecipient(notification.getRecipient());
        po.setChannel(notification.getChannel().name());
        po.setContent(notification.getContent());
        po.setStatus(notification.getStatus().name());
        po.setRetryCount(notification.getRetryCount());
        po.setErrorMessage(notification.getErrorMessage());
        po.setSentAt(notification.getSentAt());
        return po;
    }
}
