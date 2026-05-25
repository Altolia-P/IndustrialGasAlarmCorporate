package com.niit.industrialgasalarmcorporate.assembler;

import com.niit.industrialgasalarmcorporate.application.message.dto.SubmitMessageDTO;
import com.niit.industrialgasalarmcorporate.application.message.vo.MessageVO;
import com.niit.industrialgasalarmcorporate.domain.message.ContactMessage;

import java.time.format.DateTimeFormatter;

public final class MessageAssembler {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private MessageAssembler() {
    }

    public static ContactMessage toEntity(SubmitMessageDTO dto, String ip) {
        return new ContactMessage(dto.getName(), dto.getPhone(), dto.getContent(), ip);
    }

    public static MessageVO toVO(ContactMessage message) {
        MessageVO vo = new MessageVO();
        vo.setMessageUuid(message.getMessageUuid());
        vo.setName(message.getName());
        vo.setPhone(maskPhone(message.getPhone()));
        vo.setContent(message.getContent());
        vo.setStatus(message.getStatus().name());
        vo.setProcessor(message.getProcessor());
        vo.setRemark(message.getRemark());
        vo.setAssignedStaffUuid(message.getAssignedStaffUuid());
        vo.setAssignedStaffName(message.getAssignedStaffName());
        if (message.getSubmittedAt() != null) {
            vo.setSubmittedAt(message.getSubmittedAt().format(FORMATTER));
        }
        if (message.getProcessedAt() != null) {
            vo.setProcessedAt(message.getProcessedAt().format(FORMATTER));
        }
        return vo;
    }

    private static String maskPhone(String phone) {
        if (phone != null && phone.length() == 11) {
            return phone.substring(0, 3) + "****" + phone.substring(7);
        }
        return phone;
    }
}
