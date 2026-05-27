package com.niit.industrialgasalarmcorporate.domain.message;

import java.time.LocalDateTime;
import java.util.UUID;

public class ContactMessage {

    private final String messageUuid;
    private final String name;
    private final String phone;
    private final String content;
    private final String ip;
    private MessageStatus status;
    private String processor;
    private String remark;
    private String assignedStaffUuid;
    private String assignedStaffName;
    private final LocalDateTime submittedAt;
    private LocalDateTime processedAt;

    public ContactMessage(String name, String phone, String content, String ip) {
        this.messageUuid = UUID.randomUUID().toString();
        this.name = name;
        this.phone = phone;
        this.content = content;
        this.ip = ip;
        this.status = MessageStatus.PENDING;
        this.submittedAt = LocalDateTime.now();
    }

    public ContactMessage(String messageUuid, String name, String phone, String content,
                          String ip, MessageStatus status, String processor, String remark,
                          String assignedStaffUuid, String assignedStaffName,
                          LocalDateTime submittedAt, LocalDateTime processedAt) {
        this.messageUuid = messageUuid;
        this.name = name;
        this.phone = phone;
        this.content = content;
        this.ip = ip;
        this.status = status;
        this.processor = processor;
        this.remark = remark;
        this.assignedStaffUuid = assignedStaffUuid;
        this.assignedStaffName = assignedStaffName;
        this.submittedAt = submittedAt;
        this.processedAt = processedAt;
    }

    public void assign(String staffUuid, String staffName) {
        this.assignedStaffUuid = staffUuid;
        this.assignedStaffName = staffName;
        this.status = MessageStatus.IN_PROGRESS;
    }

    public void markProcessed(String processor, String remark) {
        this.status = MessageStatus.PROCESSED;
        this.processor = processor;
        this.remark = remark;
        this.processedAt = LocalDateTime.now();
    }

    public String getMessageUuid() {
        return messageUuid;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getContent() {
        return content;
    }

    public String getIp() {
        return ip;
    }

    public MessageStatus getStatus() {
        return status;
    }

    public String getProcessor() {
        return processor;
    }

    public String getRemark() {
        return remark;
    }

    public String getAssignedStaffUuid() {
        return assignedStaffUuid;
    }

    public String getAssignedStaffName() {
        return assignedStaffName;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }
}
