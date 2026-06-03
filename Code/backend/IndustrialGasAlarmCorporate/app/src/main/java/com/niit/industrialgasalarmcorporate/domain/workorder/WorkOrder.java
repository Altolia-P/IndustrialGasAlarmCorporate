package com.niit.industrialgasalarmcorporate.domain.workorder;

import java.time.LocalDateTime;
import java.util.UUID;

public class WorkOrder {

    private final String workOrderUuid;
    private String title;
    private WorkOrderType type;
    private String description;
    private WorkOrderStatus status;
    private WorkOrderPriority priority;
    private String customerName;
    private String customerPhone;
    private String assignedStaffUuid;
    private String assignedStaffName;
    private String resolution;
    private LocalDateTime completedAt;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer version;

    public WorkOrder(String title, WorkOrderType type, String description, WorkOrderPriority priority,
                     String customerName, String customerPhone,
                     String assignedStaffUuid, String assignedStaffName) {
        this.workOrderUuid = UUID.randomUUID().toString();
        this.title = title;
        this.type = type;
        this.description = description;
        this.status = WorkOrderStatus.PENDING;
        this.priority = priority;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.assignedStaffUuid = assignedStaffUuid;
        this.assignedStaffName = assignedStaffName;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public WorkOrder(String workOrderUuid, String title, WorkOrderType type, String description,
                     WorkOrderStatus status, WorkOrderPriority priority,
                     String customerName, String customerPhone,
                     String assignedStaffUuid, String assignedStaffName, String resolution,
                     LocalDateTime completedAt, LocalDateTime createdAt, LocalDateTime updatedAt,
                     Integer version) {
        this.workOrderUuid = workOrderUuid;
        this.title = title;
        this.type = type;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.assignedStaffUuid = assignedStaffUuid;
        this.assignedStaffName = assignedStaffName;
        this.resolution = resolution;
        this.completedAt = completedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    public String getWorkOrderUuid() { return workOrderUuid; }
    public String getTitle() { return title; }
    public WorkOrderType getType() { return type; }
    public String getDescription() { return description; }
    public WorkOrderStatus getStatus() { return status; }
    public WorkOrderPriority getPriority() { return priority; }
    public String getCustomerName() { return customerName; }
    public String getCustomerPhone() { return customerPhone; }
    public String getAssignedStaffUuid() { return assignedStaffUuid; }
    public String getAssignedStaffName() { return assignedStaffName; }
    public String getResolution() { return resolution; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public Integer getVersion() { return version; }

    public void update(String title, WorkOrderType type, String description, WorkOrderPriority priority,
                       String customerName, String customerPhone,
                       String assignedStaffUuid, String assignedStaffName, String resolution) {
        if (this.status == WorkOrderStatus.COMPLETED) {
            throw new IllegalStateException("已完成的工单不能修改");
        }
        if (title != null && !title.isBlank()) {
            this.title = title;
        }
        if (type != null) {
            this.type = type;
        }
        if (description != null) {
            this.description = description;
        }
        if (priority != null) {
            this.priority = priority;
        }
        if (customerName != null && !customerName.isBlank()) {
            this.customerName = customerName;
        }
        if (customerPhone != null) {
            this.customerPhone = customerPhone;
        }
        if (assignedStaffUuid != null) {
            this.assignedStaffUuid = assignedStaffUuid;
        }
        if (assignedStaffName != null) {
            this.assignedStaffName = assignedStaffName;
        }
        if (resolution != null) {
            this.resolution = resolution;
        }
    }

    public void assign(String staffUuid, String staffName) {
        if (this.status == WorkOrderStatus.COMPLETED) {
            throw new IllegalStateException("已完成的工单不能重新分配");
        }
        this.assignedStaffUuid = staffUuid;
        this.assignedStaffName = staffName;
        if (this.status == WorkOrderStatus.PENDING) {
            this.status = WorkOrderStatus.IN_PROGRESS;
        }
    }

    public void complete(String resolution) {
        if (this.status == WorkOrderStatus.COMPLETED) {
            throw new IllegalStateException("工单已完成，不能重复完成");
        }
        if (this.assignedStaffUuid == null) {
            throw new IllegalStateException("工单未指派，请先指派处理人");
        }
        this.resolution = resolution;
        this.status = WorkOrderStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }
}
