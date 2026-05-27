package com.niit.industrialgasalarmcorporate.application.workorder.service.impl;

import com.niit.industrialgasalarmcorporate.application.workorder.dto.*;
import com.niit.industrialgasalarmcorporate.application.workorder.service.WorkOrderService;
import com.niit.industrialgasalarmcorporate.application.workorder.vo.WorkOrderVO;
import com.niit.industrialgasalarmcorporate.assembler.WorkOrderAssembler;
import com.niit.industrialgasalarmcorporate.common.base.Page;
import com.niit.industrialgasalarmcorporate.common.enums.ErrorCode;
import com.niit.industrialgasalarmcorporate.common.exception.BusinessException;
import com.niit.industrialgasalarmcorporate.domain.workorder.WorkOrder;
import com.niit.industrialgasalarmcorporate.domain.workorder.WorkOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkOrderServiceImpl implements WorkOrderService {

    private final WorkOrderRepository workOrderRepository;

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
        workOrder.assign(dto.getStaffUuid(), dto.getStaffName());
        workOrderRepository.save(workOrder);
    }

    @Override
    @Transactional
    public void completeWorkOrder(String workOrderUuid, CompleteWorkOrderDTO dto) {
        WorkOrder workOrder = workOrderRepository.findById(workOrderUuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.WORK_ORDER_NOT_FOUND));
        workOrder.complete(dto.getResolution());
        workOrderRepository.save(workOrder);
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
