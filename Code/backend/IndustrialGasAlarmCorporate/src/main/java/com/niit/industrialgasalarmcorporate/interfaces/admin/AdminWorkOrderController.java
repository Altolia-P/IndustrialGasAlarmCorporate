package com.niit.industrialgasalarmcorporate.interfaces.admin;

import com.niit.industrialgasalarmcorporate.application.workorder.dto.*;
import com.niit.industrialgasalarmcorporate.application.workorder.service.WorkOrderService;
import com.niit.industrialgasalarmcorporate.application.workorder.vo.WorkOrderVO;
import com.niit.industrialgasalarmcorporate.common.base.Page;
import com.niit.industrialgasalarmcorporate.common.base.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminWorkOrderController {

    private final WorkOrderService workOrderService;

    @GetMapping("/workorders")
    public Result<Page<WorkOrderVO>> getWorkOrders(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return Result.ok(workOrderService.findWorkOrders(title, type, status, page, size));
    }

    @GetMapping("/workorders/{uuid}")
    public Result<WorkOrderVO> getWorkOrder(@PathVariable String uuid) {
        return Result.ok(workOrderService.getWorkOrder(uuid));
    }

    @PostMapping("/workorders")
    public Result<WorkOrderVO> createWorkOrder(@Valid @RequestBody CreateWorkOrderDTO dto) {
        return Result.ok("创建成功", workOrderService.createWorkOrder(dto));
    }

    @PutMapping("/workorders/{uuid}")
    public Result<WorkOrderVO> updateWorkOrder(@PathVariable String uuid,
                                               @Valid @RequestBody UpdateWorkOrderDTO dto) {
        return Result.ok("更新成功", workOrderService.updateWorkOrder(uuid, dto));
    }

    @PutMapping("/workorders/{uuid}/assign")
    public Result<Void> assignWorkOrder(@PathVariable String uuid,
                                        @Valid @RequestBody AssignWorkOrderDTO dto) {
        workOrderService.assignWorkOrder(uuid, dto);
        return Result.ok("指派成功", null);
    }

    @PutMapping("/workorders/{uuid}/complete")
    public Result<Void> completeWorkOrder(@PathVariable String uuid,
                                          @Valid @RequestBody CompleteWorkOrderDTO dto) {
        workOrderService.completeWorkOrder(uuid, dto);
        return Result.ok("处理完成", null);
    }

    @DeleteMapping("/workorders/{uuid}")
    public Result<Void> deleteWorkOrder(@PathVariable String uuid) {
        workOrderService.deleteWorkOrder(uuid);
        return Result.ok("删除成功", null);
    }
}
