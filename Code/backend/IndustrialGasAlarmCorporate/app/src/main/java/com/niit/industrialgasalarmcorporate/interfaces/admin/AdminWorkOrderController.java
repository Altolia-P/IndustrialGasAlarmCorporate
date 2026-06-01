package com.niit.industrialgasalarmcorporate.interfaces.admin;

import com.niit.industrialgasalarmcorporate.application.comment.dto.CreateCommentDTO;
import com.niit.industrialgasalarmcorporate.application.comment.service.CommentService;
import com.niit.industrialgasalarmcorporate.application.comment.vo.CommentVO;
import com.niit.industrialgasalarmcorporate.application.workorder.dto.*;
import com.niit.industrialgasalarmcorporate.application.workorder.service.WorkOrderService;
import com.niit.industrialgasalarmcorporate.application.workorder.vo.WorkOrderVO;
import com.niit.industrialgasalarmcorporate.common.base.Page;
import com.niit.industrialgasalarmcorporate.common.base.Result;
import com.niit.industrialgasalarmcorporate.domain.comment.CommentAuthorType;
import com.niit.industrialgasalarmcorporate.domain.comment.CommentTargetType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminWorkOrderController {

    private final WorkOrderService workOrderService;
    private final CommentService commentService;

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

    @GetMapping("/workorders/{uuid}/comments")
    public Result<List<CommentVO>> getWorkOrderComments(@PathVariable String uuid) {
        return Result.ok(commentService.findByTarget(CommentTargetType.WORK_ORDER, uuid));
    }

    @PostMapping("/workorders/{uuid}/comments")
    public Result<CommentVO> addWorkOrderComment(@PathVariable String uuid,
                                                  @Valid @RequestBody CreateCommentDTO dto,
                                                  @RequestAttribute String userUuid,
                                                  @RequestAttribute String username) {
        return Result.ok("评论成功", commentService.addComment(
                CommentTargetType.WORK_ORDER, uuid, CommentAuthorType.ADMIN, userUuid, username, dto));
    }
}
