package com.niit.industrialgasalarmcorporate.application.workorder.service;

import com.niit.industrialgasalarmcorporate.application.workorder.dto.*;
import com.niit.industrialgasalarmcorporate.application.workorder.vo.WorkOrderVO;
import com.niit.industrialgasalarmcorporate.common.base.Page;

public interface WorkOrderService {

    Page<WorkOrderVO> findWorkOrders(String title, String type, String status, int page, int size);

    WorkOrderVO getWorkOrder(String workOrderUuid);

    WorkOrderVO createWorkOrder(CreateWorkOrderDTO dto);

    WorkOrderVO updateWorkOrder(String workOrderUuid, UpdateWorkOrderDTO dto);

    void assignWorkOrder(String workOrderUuid, AssignWorkOrderDTO dto);

    void completeWorkOrder(String workOrderUuid, CompleteWorkOrderDTO dto);

    void deleteWorkOrder(String workOrderUuid);

    Page<WorkOrderVO> findMyTasks(String staffUuid, String status, int page, int size);

    Page<WorkOrderVO> findUserWorkOrders(String customerName, int page, int size);
}
