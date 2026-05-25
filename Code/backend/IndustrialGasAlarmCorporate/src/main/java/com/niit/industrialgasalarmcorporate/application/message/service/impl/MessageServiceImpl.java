package com.niit.industrialgasalarmcorporate.application.message.service.impl;

import com.niit.industrialgasalarmcorporate.application.message.dto.AssignMessageDTO;
import com.niit.industrialgasalarmcorporate.application.message.dto.BatchProcessDTO;
import com.niit.industrialgasalarmcorporate.application.message.dto.ProcessMessageDTO;
import com.niit.industrialgasalarmcorporate.application.message.dto.SubmitMessageDTO;
import com.niit.industrialgasalarmcorporate.application.message.service.MessageService;
import com.niit.industrialgasalarmcorporate.application.message.vo.MessageVO;
import com.niit.industrialgasalarmcorporate.assembler.MessageAssembler;
import com.niit.industrialgasalarmcorporate.common.base.Page;
import com.niit.industrialgasalarmcorporate.common.enums.ErrorCode;
import com.niit.industrialgasalarmcorporate.common.exception.BusinessException;
import com.niit.industrialgasalarmcorporate.common.exception.MessageNotFoundException;
import com.niit.industrialgasalarmcorporate.domain.message.ContactMessage;
import com.niit.industrialgasalarmcorporate.domain.message.MessageRepository;
import com.niit.industrialgasalarmcorporate.domain.message.MessageStatus;
import com.niit.industrialgasalarmcorporate.infrastructure.redis.MessageRateLimitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final MessageRateLimitRepository rateLimitRepository;

    @Override
    @Transactional
    public String submitMessage(SubmitMessageDTO dto, String ip) {
        if (!rateLimitRepository.tryAcquirePhone(dto.getPhone())) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "您已提交过，请稍后再试");
        }
        if (!rateLimitRepository.tryAcquireIp(ip)) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "操作过于频繁，请稍后重试");
        }
        ContactMessage message = MessageAssembler.toEntity(dto, ip);
        messageRepository.save(message);
        log.info("留言提交成功: messageUuid={}, phone={}, ip={}", message.getMessageUuid(), dto.getPhone(), ip);
        return message.getMessageUuid();
    }

    @Override
    @Transactional
    public void assignMessage(String messageUuid, AssignMessageDTO dto) {
        ContactMessage message = messageRepository.findById(messageUuid)
                .orElseThrow(() -> new MessageNotFoundException(messageUuid));
        message.assign(dto.getStaffUuid(), dto.getStaffName());
        messageRepository.save(message);
    }

    @Override
    @Transactional
    public void markProcessed(String messageUuid, ProcessMessageDTO dto, String processor) {
        ContactMessage message = messageRepository.findById(messageUuid)
                .orElseThrow(() -> new MessageNotFoundException(messageUuid));
        message.markProcessed(processor, dto.getRemark());
        messageRepository.save(message);
    }

    @Override
    @Transactional
    public void batchProcess(BatchProcessDTO dto, String processor) {
        for (String uuid : dto.getUuids()) {
            ContactMessage message = messageRepository.findById(uuid)
                    .orElseThrow(() -> new MessageNotFoundException(uuid));
            message.markProcessed(processor, dto.getRemark() != null ? dto.getRemark() : "批量处理");
            messageRepository.save(message);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public MessageVO getMessage(String messageUuid) {
        ContactMessage message = messageRepository.findById(messageUuid)
                .orElseThrow(() -> new MessageNotFoundException(messageUuid));
        return MessageAssembler.toVO(message);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MessageVO> findMessages(String name, String phone, String status, int page, int size) {
        MessageStatus msgStatus = status != null ? MessageStatus.valueOf(status) : null;
        Page<ContactMessage> domainPage;
        if ((name != null && !name.isBlank()) || (phone != null && !phone.isBlank())) {
            domainPage = messageRepository.findAllWithFilter(name, phone, msgStatus, page, size);
        } else {
            domainPage = messageRepository.findAll(page, size, msgStatus);
        }
        return new Page<>(
                domainPage.getContent().stream().map(MessageAssembler::toVO).collect(Collectors.toList()),
                domainPage.getTotalElements(),
                domainPage.getSize(),
                domainPage.getNumber()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MessageVO> findUserMessages(String name, int page, int size) {
        Page<ContactMessage> domainPage = messageRepository.findAllWithFilter(name, null, null, page, size);
        return new Page<>(
                domainPage.getContent().stream().map(MessageAssembler::toVO).collect(Collectors.toList()),
                domainPage.getTotalElements(),
                domainPage.getSize(),
                domainPage.getNumber()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MessageVO> findStaffMessages(String staffUuid, int page, int size) {
        Page<ContactMessage> domainPage = messageRepository.findByAssignedStaffUuid(staffUuid, page, size);
        return new Page<>(
                domainPage.getContent().stream().map(MessageAssembler::toVO).collect(Collectors.toList()),
                domainPage.getTotalElements(),
                domainPage.getSize(),
                domainPage.getNumber()
        );
    }
}
