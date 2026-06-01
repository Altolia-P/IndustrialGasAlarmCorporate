package com.niit.industrialgasalarmcorporate.infrastructure.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niit.industrialgasalarmcorporate.domain.workorder.*;
import com.niit.industrialgasalarmcorporate.infrastructure.repository.mapper.WorkOrderMapper;
import com.niit.industrialgasalarmcorporate.infrastructure.repository.po.WorkOrderPO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class WorkOrderRepositoryImpl implements WorkOrderRepository {

    private final WorkOrderMapper workOrderMapper;

    @Override
    public Optional<WorkOrder> findById(String workOrderUuid) {
        WorkOrderPO po = workOrderMapper.selectById(workOrderUuid);
        if (po == null) {
            return Optional.empty();
        }
        return Optional.of(toDomain(po));
    }

    @Override
    public com.niit.industrialgasalarmcorporate.common.base.Page<WorkOrder> findAllWithFilter(
            String title, String type, String status, int page, int size) {
        LambdaQueryWrapper<WorkOrderPO> wrapper = new LambdaQueryWrapper<>();
        if (title != null && !title.isBlank()) {
            wrapper.like(WorkOrderPO::getTitle, title);
        }
        if (type != null && !type.isBlank()) {
            wrapper.eq(WorkOrderPO::getType, type);
        }
        if (status != null && !status.isBlank()) {
            wrapper.eq(WorkOrderPO::getStatus, status);
        }
        wrapper.orderByDesc(WorkOrderPO::getCreatedAt);
        Page<WorkOrderPO> mpPage = new Page<>(page, size);
        Page<WorkOrderPO> result = workOrderMapper.selectPage(mpPage, wrapper);
        List<WorkOrder> list = result.getRecords().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
        return new com.niit.industrialgasalarmcorporate.common.base.Page<>(
                list, result.getTotal(), (int) result.getSize(), (int) result.getCurrent());
    }

    @Override
    public com.niit.industrialgasalarmcorporate.common.base.Page<WorkOrder> findByStaffUuid(
            String staffUuid, String status, int page, int size) {
        LambdaQueryWrapper<WorkOrderPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WorkOrderPO::getAssignedStaffUuid, staffUuid);
        if (status != null && !status.isBlank()) {
            wrapper.eq(WorkOrderPO::getStatus, status);
        }
        wrapper.orderByDesc(WorkOrderPO::getCreatedAt);
        Page<WorkOrderPO> mpPage = new Page<>(page, size);
        Page<WorkOrderPO> result = workOrderMapper.selectPage(mpPage, wrapper);
        List<WorkOrder> list = result.getRecords().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
        return new com.niit.industrialgasalarmcorporate.common.base.Page<>(
                list, result.getTotal(), (int) result.getSize(), (int) result.getCurrent());
    }

    @Override
    public com.niit.industrialgasalarmcorporate.common.base.Page<WorkOrder> findByCustomerName(
            String customerName, int page, int size) {
        LambdaQueryWrapper<WorkOrderPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(WorkOrderPO::getCustomerName, customerName)
                .orderByDesc(WorkOrderPO::getCreatedAt);
        Page<WorkOrderPO> mpPage = new Page<>(page, size);
        Page<WorkOrderPO> result = workOrderMapper.selectPage(mpPage, wrapper);
        List<WorkOrder> list = result.getRecords().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
        return new com.niit.industrialgasalarmcorporate.common.base.Page<>(
                list, result.getTotal(), (int) result.getSize(), (int) result.getCurrent());
    }

    @Override
    public void save(WorkOrder workOrder) {
        WorkOrderPO po = toPO(workOrder);
        WorkOrderPO existing = workOrderMapper.selectById(workOrder.getWorkOrderUuid());
        if (existing != null) {
            workOrderMapper.updateById(po);
        } else {
            workOrderMapper.insert(po);
        }
    }

    @Override
    public void deleteById(String workOrderUuid) {
        workOrderMapper.deleteById(workOrderUuid);
    }

    @Override
    public long countByStaffAndStatus(String staffUuid, WorkOrderStatus status) {
        LambdaQueryWrapper<WorkOrderPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WorkOrderPO::getAssignedStaffUuid, staffUuid)
                .eq(WorkOrderPO::getStatus, status.name());
        return workOrderMapper.selectCount(wrapper);
    }

    @Override
    public long countByStatus(WorkOrderStatus status) {
        LambdaQueryWrapper<WorkOrderPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WorkOrderPO::getStatus, status.name());
        return workOrderMapper.selectCount(wrapper);
    }

    @Override
    public java.util.Map<WorkOrderStatus, Long> countGroupByStatus() {
        java.util.Map<WorkOrderStatus, Long> map = new java.util.LinkedHashMap<>();
        for (WorkOrderStatus status : WorkOrderStatus.values()) {
            map.put(status, countByStatus(status));
        }
        return map;
    }

    @Override
    public List<WorkOrder> findByCustomerPhone(String phone) {
        LambdaQueryWrapper<WorkOrderPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WorkOrderPO::getCustomerPhone, phone)
                .orderByDesc(WorkOrderPO::getCreatedAt);
        return workOrderMapper.selectList(wrapper).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private WorkOrder toDomain(WorkOrderPO po) {
        return new WorkOrder(
                po.getWorkOrderUuid(),
                po.getTitle(),
                WorkOrderType.valueOf(po.getType()),
                po.getDescription(),
                WorkOrderStatus.valueOf(po.getStatus()),
                WorkOrderPriority.valueOf(po.getPriority()),
                po.getCustomerName(),
                po.getCustomerPhone(),
                po.getAssignedStaffUuid(),
                po.getAssignedStaffName(),
                po.getResolution(),
                po.getCompletedAt(),
                po.getCreatedAt(),
                po.getUpdatedAt()
        );
    }

    private WorkOrderPO toPO(WorkOrder workOrder) {
        WorkOrderPO po = new WorkOrderPO();
        po.setWorkOrderUuid(workOrder.getWorkOrderUuid());
        po.setTitle(workOrder.getTitle());
        po.setType(workOrder.getType().name());
        po.setDescription(workOrder.getDescription());
        po.setStatus(workOrder.getStatus().name());
        po.setPriority(workOrder.getPriority().name());
        po.setAssignedStaffUuid(workOrder.getAssignedStaffUuid());
        po.setAssignedStaffName(workOrder.getAssignedStaffName());
        po.setCustomerName(workOrder.getCustomerName());
        po.setCustomerPhone(workOrder.getCustomerPhone());
        po.setResolution(workOrder.getResolution());
        po.setCompletedAt(workOrder.getCompletedAt());
        return po;
    }
}
