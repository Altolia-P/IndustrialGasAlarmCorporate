package com.niit.industrialgasalarmcorporate.assembler;

import com.niit.industrialgasalarmcorporate.application.workorder.dto.CreateWorkOrderDTO;
import com.niit.industrialgasalarmcorporate.application.workorder.dto.UpdateWorkOrderDTO;
import com.niit.industrialgasalarmcorporate.application.workorder.vo.WorkOrderVO;
import com.niit.industrialgasalarmcorporate.domain.workorder.WorkOrder;
import com.niit.industrialgasalarmcorporate.domain.workorder.WorkOrderPriority;
import com.niit.industrialgasalarmcorporate.domain.workorder.WorkOrderType;

import java.time.format.DateTimeFormatter;

public final class WorkOrderAssembler {

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private WorkOrderAssembler() {}

    public static WorkOrder toEntity(CreateWorkOrderDTO dto) {
        return new WorkOrder(
                dto.getTitle(),
                WorkOrderType.valueOf(dto.getType()),
                dto.getDescription(),
                WorkOrderPriority.valueOf(dto.getPriority()),
                dto.getCustomerName(),
                dto.getCustomerPhone(),
                dto.getAssignedStaffUuid(),
                dto.getAssignedStaffName()
        );
    }

    public static void updateEntity(WorkOrder workOrder, UpdateWorkOrderDTO dto) {
        workOrder.update(
                dto.getTitle(),
                dto.getType() != null ? WorkOrderType.valueOf(dto.getType()) : null,
                dto.getDescription(),
                dto.getPriority() != null ? WorkOrderPriority.valueOf(dto.getPriority()) : null,
                dto.getCustomerName(),
                dto.getCustomerPhone(),
                dto.getAssignedStaffUuid(),
                dto.getAssignedStaffName(),
                dto.getResolution()
        );
    }

    public static WorkOrderVO toVO(WorkOrder workOrder) {
        WorkOrderVO vo = new WorkOrderVO();
        vo.setWorkOrderUuid(workOrder.getWorkOrderUuid());
        vo.setTitle(workOrder.getTitle());
        vo.setType(workOrder.getType().name());
        vo.setDescription(workOrder.getDescription());
        vo.setStatus(workOrder.getStatus().name());
        vo.setPriority(workOrder.getPriority().name());
        vo.setCustomerName(workOrder.getCustomerName());
        vo.setCustomerPhone(workOrder.getCustomerPhone());
        vo.setAssignedStaffUuid(workOrder.getAssignedStaffUuid());
        vo.setAssignedStaffName(workOrder.getAssignedStaffName());
        vo.setResolution(workOrder.getResolution());
        vo.setCreatedAt(workOrder.getCreatedAt().format(DTF));
        vo.setUpdatedAt(workOrder.getUpdatedAt().format(DTF));
        return vo;
    }
}
