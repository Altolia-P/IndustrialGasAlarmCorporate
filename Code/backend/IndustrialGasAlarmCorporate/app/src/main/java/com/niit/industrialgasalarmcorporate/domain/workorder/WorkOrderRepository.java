package com.niit.industrialgasalarmcorporate.domain.workorder;

import com.niit.industrialgasalarmcorporate.common.base.Page;

import java.util.Optional;

public interface WorkOrderRepository {

    Optional<WorkOrder> findById(String workOrderUuid);

    Page<WorkOrder> findAllWithFilter(String title, String type, String status, int page, int size);

    Page<WorkOrder> findByStaffUuid(String staffUuid, String status, int page, int size);

    Page<WorkOrder> findByCustomerName(String customerName, int page, int size);

    void save(WorkOrder workOrder);

    void deleteById(String workOrderUuid);
}
