package com.niit.industrialgasalarmcorporate.application.workorder.service.impl;

import com.niit.industrialgasalarmcorporate.application.workorder.dto.*;
import com.niit.industrialgasalarmcorporate.application.workorder.service.WorkOrderService;
import com.niit.industrialgasalarmcorporate.application.workorder.vo.WorkOrderVO;
import com.niit.industrialgasalarmcorporate.assembler.WorkOrderAssembler;
import com.niit.industrialgasalarmcorporate.common.base.Page;
import com.niit.industrialgasalarmcorporate.common.enums.ErrorCode;
import com.niit.industrialgasalarmcorporate.common.exception.BusinessException;
import com.niit.industrialgasalarmcorporate.domain.message.MessageRepository;
import com.niit.industrialgasalarmcorporate.domain.message.MessageStatus;
import com.niit.industrialgasalarmcorporate.domain.staff.StaffRepository;
import com.niit.industrialgasalarmcorporate.domain.staff.StaffStatus;
import com.niit.industrialgasalarmcorporate.domain.workorder.WorkOrder;
import com.niit.industrialgasalarmcorporate.domain.workorder.WorkOrderRepository;
import com.niit.industrialgasalarmcorporate.domain.workorder.WorkOrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkOrderServiceImpl implements WorkOrderService {

    private final WorkOrderRepository workOrderRepository;
    private final StaffRepository staffRepository;
    private final MessageRepository messageRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<WorkOrderVO> findWorkOrders(String title, String type, String status, int page, int size) {
        Page<WorkOrder> domainPage = workOrderRepository.findAllWithFilter(title, type, status, page, size);
        return new Page<>(
                domainPage.getContent().stream().map(WorkOrderAssembler::toVO).collect(Collectors.toList()),
                domainPage.getTotalElements(),
                domainPage.getSize(),
                domainPage.getNumber()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public WorkOrderVO getWorkOrder(String workOrderUuid) {
        WorkOrder workOrder = workOrderRepository.findById(workOrderUuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.WORK_ORDER_NOT_FOUND));
        return WorkOrderAssembler.toVO(workOrder);
    }

    @Override
    @Transactional
    public WorkOrderVO createWorkOrder(CreateWorkOrderDTO dto) {
        WorkOrder workOrder = WorkOrderAssembler.toEntity(dto);
        workOrderRepository.save(workOrder);
        return WorkOrderAssembler.toVO(workOrder);
    }

    @Override
    @Transactional
    public WorkOrderVO updateWorkOrder(String workOrderUuid, UpdateWorkOrderDTO dto) {
        WorkOrder workOrder = workOrderRepository.findById(workOrderUuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.WORK_ORDER_NOT_FOUND));
        WorkOrderAssembler.updateEntity(workOrder, dto);
        workOrderRepository.save(workOrder);
        return WorkOrderAssembler.toVO(workOrder);
    }

    @Override
    @Transactional
    public void assignWorkOrder(String workOrderUuid, AssignWorkOrderDTO dto) {
        WorkOrder workOrder = workOrderRepository.findById(workOrderUuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.WORK_ORDER_NOT_FOUND));
        var staff = staffRepository.findById(dto.getStaffUuid())
                .orElseThrow(() -> new BusinessException(ErrorCode.STAFF_NOT_FOUND));
        if (!staff.isAvailable()) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "该员工当前状态不可用（休假/出差中）");
        }
        workOrder.assign(dto.getStaffUuid(), dto.getStaffName());
        workOrderRepository.save(workOrder);
        staff.changeStatus(StaffStatus.WORKING);
        staffRepository.save(staff);
    }

    @Override
    @Transactional
    public void completeWorkOrder(String workOrderUuid, CompleteWorkOrderDTO dto) {
        WorkOrder workOrder = workOrderRepository.findById(workOrderUuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.WORK_ORDER_NOT_FOUND));
        doComplete(workOrder, dto.getResolution());
    }

    @Override
    @Transactional
    public WorkOrderVO getMyTaskDetail(String staffUuid, String workOrderUuid) {
        WorkOrder workOrder = workOrderRepository.findById(workOrderUuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.WORK_ORDER_NOT_FOUND));
        if (!staffUuid.equals(workOrder.getAssignedStaffUuid())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "该工单未分配给您");
        }
        return WorkOrderAssembler.toVO(workOrder);
    }

    @Override
    @Transactional
    public void completeMyTask(String staffUuid, String workOrderUuid, CompleteWorkOrderDTO dto) {
        WorkOrder workOrder = workOrderRepository.findById(workOrderUuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.WORK_ORDER_NOT_FOUND));
        if (!staffUuid.equals(workOrder.getAssignedStaffUuid())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "该工单未分配给您，无法完成");
        }
        doComplete(workOrder, dto.getResolution());
    }

    private void doComplete(WorkOrder workOrder, String resolution) {
        String staffUuid = workOrder.getAssignedStaffUuid();
        workOrder.complete(resolution);
        workOrderRepository.save(workOrder);

        if (staffUuid != null) {
            long remainingWo = workOrderRepository.countByStaffAndStatus(staffUuid, WorkOrderStatus.IN_PROGRESS);
            long remainingMsg = messageRepository.countByStaffAndStatus(staffUuid, MessageStatus.IN_PROGRESS);
            if (remainingWo == 0 && remainingMsg == 0) {
                staffRepository.findById(staffUuid).ifPresent(staff -> {
                    staff.changeStatus(StaffStatus.STANDBY);
                    staffRepository.save(staff);
                });
            }
        }
    }

    @Override
    @Transactional
    public void deleteWorkOrder(String workOrderUuid) {
        if (workOrderRepository.findById(workOrderUuid).isEmpty()) {
            throw new BusinessException(ErrorCode.WORK_ORDER_NOT_FOUND);
        }
        workOrderRepository.deleteById(workOrderUuid);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WorkOrderVO> findMyTasks(String staffUuid, String status, int page, int size) {
        Page<WorkOrder> domainPage = workOrderRepository.findByStaffUuid(staffUuid, status, page, size);
        return new Page<>(
                domainPage.getContent().stream().map(WorkOrderAssembler::toVO).collect(Collectors.toList()),
                domainPage.getTotalElements(),
                domainPage.getSize(),
                domainPage.getNumber()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WorkOrderVO> findUserWorkOrders(String customerName, int page, int size) {
        Page<WorkOrder> domainPage = workOrderRepository.findByCustomerName(customerName, page, size);
        return new Page<>(
                domainPage.getContent().stream().map(WorkOrderAssembler::toVO).collect(Collectors.toList()),
                domainPage.getTotalElements(),
                domainPage.getSize(),
                domainPage.getNumber()
        );
    }
}
