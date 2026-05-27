package com.niit.industrialgasalarmcorporate.infrastructure.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.niit.industrialgasalarmcorporate.common.base.Page;
import com.niit.industrialgasalarmcorporate.domain.message.ContactMessage;
import com.niit.industrialgasalarmcorporate.domain.message.MessageRepository;
import com.niit.industrialgasalarmcorporate.domain.message.MessageStatus;
import com.niit.industrialgasalarmcorporate.infrastructure.repository.mapper.ContactMessageMapper;
import com.niit.industrialgasalarmcorporate.infrastructure.repository.po.ContactMessagePO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class MessageRepositoryImpl implements MessageRepository {

    private final ContactMessageMapper contactMessageMapper;

    @Override
    public void save(ContactMessage message) {
        ContactMessagePO po = toPO(message);
        ContactMessagePO existing = contactMessageMapper.selectById(message.getMessageUuid());
        if (existing != null) {
            contactMessageMapper.updateById(po);
        } else {
            contactMessageMapper.insert(po);
        }
    }

    @Override
    public Optional<ContactMessage> findById(String messageUuid) {
        ContactMessagePO po = contactMessageMapper.selectById(messageUuid);
        if (po == null) {
            return Optional.empty();
        }
        return Optional.of(toDomain(po));
    }

    @Override
    public Page<ContactMessage> findAll(int page, int size, MessageStatus status) {
        LambdaQueryWrapper<ContactMessagePO> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(ContactMessagePO::getStatus, status.name());
        }
        wrapper.orderByDesc(ContactMessagePO::getSubmittedAt);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ContactMessagePO> mpPage =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ContactMessagePO> result =
                contactMessageMapper.selectPage(mpPage, wrapper);
        List<ContactMessage> messages = result.getRecords().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
        return new Page<>(messages, result.getTotal(), (int) result.getSize(), (int) result.getCurrent());
    }

    @Override
    public Page<ContactMessage> findAllWithFilter(String name, String phone, MessageStatus status,
                                                         int page, int size) {
        LambdaQueryWrapper<ContactMessagePO> wrapper = new LambdaQueryWrapper<>();
        if (name != null && !name.isBlank()) {
            wrapper.like(ContactMessagePO::getName, name);
        }
        if (phone != null && !phone.isBlank()) {
            wrapper.like(ContactMessagePO::getPhone, phone);
        }
        if (status != null) {
            wrapper.eq(ContactMessagePO::getStatus, status.name());
        }
        wrapper.orderByDesc(ContactMessagePO::getSubmittedAt);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ContactMessagePO> mpPage =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ContactMessagePO> result =
                contactMessageMapper.selectPage(mpPage, wrapper);
        List<ContactMessage> messages = result.getRecords().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
        return new Page<>(messages, result.getTotal(), (int) result.getSize(), (int) result.getCurrent());
    }

    @Override
    public boolean existsByPhoneInWindow(String phone, int seconds) {
        LambdaQueryWrapper<ContactMessagePO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ContactMessagePO::getPhone, phone)
                .ge(ContactMessagePO::getSubmittedAt, LocalDateTime.now().minusSeconds(seconds));
        return contactMessageMapper.selectCount(wrapper) > 0;
    }

    @Override
    public Page<ContactMessage> findByAssignedStaffUuid(String staffUuid, int page, int size) {
        LambdaQueryWrapper<ContactMessagePO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ContactMessagePO::getAssignedStaffUuid, staffUuid)
                .orderByDesc(ContactMessagePO::getSubmittedAt);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ContactMessagePO> mpPage =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ContactMessagePO> result =
                contactMessageMapper.selectPage(mpPage, wrapper);
        List<ContactMessage> messages = result.getRecords().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
        return new Page<>(messages, result.getTotal(), (int) result.getSize(), (int) result.getCurrent());
    }

    private ContactMessage toDomain(ContactMessagePO po) {
        return new ContactMessage(
                po.getMessageUuid(),
                po.getName(),
                po.getPhone(),
                po.getContent(),
                po.getIp(),
                MessageStatus.valueOf(po.getStatus()),
                po.getProcessor(),
                po.getRemark(),
                po.getAssignedStaffUuid(),
                po.getAssignedStaffName(),
                po.getSubmittedAt(),
                po.getProcessedAt()
        );
    }

    private ContactMessagePO toPO(ContactMessage message) {
        ContactMessagePO po = new ContactMessagePO();
        po.setMessageUuid(message.getMessageUuid());
        po.setName(message.getName());
        po.setPhone(message.getPhone());
        po.setContent(message.getContent());
        po.setIp(message.getIp());
        po.setStatus(message.getStatus().name());
        po.setProcessor(message.getProcessor());
        po.setRemark(message.getRemark());
        po.setAssignedStaffUuid(message.getAssignedStaffUuid());
        po.setAssignedStaffName(message.getAssignedStaffName());
        po.setSubmittedAt(message.getSubmittedAt());
        po.setProcessedAt(message.getProcessedAt());
        return po;
    }
}
