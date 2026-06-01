package com.niit.industrialgasalarmcorporate.interfaces.staff;

import com.niit.industrialgasalarmcorporate.application.comment.dto.CreateCommentDTO;
import com.niit.industrialgasalarmcorporate.application.comment.service.CommentService;
import com.niit.industrialgasalarmcorporate.application.comment.vo.CommentVO;
import com.niit.industrialgasalarmcorporate.application.staff.service.StaffService;
import com.niit.industrialgasalarmcorporate.application.staff.vo.StaffVO;
import com.niit.industrialgasalarmcorporate.application.workorder.dto.CompleteWorkOrderDTO;
import com.niit.industrialgasalarmcorporate.application.workorder.service.WorkOrderService;
import com.niit.industrialgasalarmcorporate.application.workorder.vo.WorkOrderVO;
import com.niit.industrialgasalarmcorporate.common.base.Page;
import com.niit.industrialgasalarmcorporate.common.base.Result;
import com.niit.industrialgasalarmcorporate.common.enums.ErrorCode;
import com.niit.industrialgasalarmcorporate.common.exception.BusinessException;
import com.niit.industrialgasalarmcorporate.domain.comment.CommentAuthorType;
import com.niit.industrialgasalarmcorporate.domain.comment.CommentTargetType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/staff")
@RequiredArgsConstructor
public class StaffWorkOrderController {

    private final WorkOrderService workOrderService;
    private final StaffService staffService;
    private final CommentService commentService;

    @GetMapping("/workorders")
    public Result<Page<WorkOrderVO>> getMyTasks(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestAttribute String userUuid) {
        StaffVO profile = staffService.getMyProfile(userUuid);
        return Result.ok(workOrderService.findMyTasks(profile.getStaffUuid(), status, page, size));
    }

    @GetMapping("/workorders/{uuid}")
    public Result<WorkOrderVO> getMyTaskDetail(@PathVariable String uuid,
                                               @RequestAttribute String userUuid) {
        StaffVO profile = staffService.getMyProfile(userUuid);
        return Result.ok(workOrderService.getMyTaskDetail(profile.getStaffUuid(), uuid));
    }

    @PutMapping("/workorders/{uuid}/complete")
    public Result<Void> completeMyTask(@PathVariable String uuid,
                                       @Valid @RequestBody CompleteWorkOrderDTO dto,
                                       @RequestAttribute String userUuid) {
        StaffVO profile = staffService.getMyProfile(userUuid);
        workOrderService.completeMyTask(profile.getStaffUuid(), uuid, dto);
        return Result.ok("处理完成", null);
    }

    @GetMapping("/workorders/{uuid}/comments")
    public Result<List<CommentVO>> getTaskComments(@PathVariable String uuid) {
        return Result.ok(commentService.findByTarget(CommentTargetType.WORK_ORDER, uuid));
    }

    @PostMapping("/workorders/{uuid}/comments")
    public Result<CommentVO> addTaskComment(@PathVariable String uuid,
                                             @Valid @RequestBody CreateCommentDTO dto,
                                             @RequestAttribute String userUuid) {
        StaffVO profile = staffService.getMyProfile(userUuid);
        WorkOrderVO wo = workOrderService.getMyTaskDetail(profile.getStaffUuid(), uuid);
        return Result.ok("评论成功", commentService.addComment(
                CommentTargetType.WORK_ORDER, uuid,
                CommentAuthorType.STAFF, profile.getStaffUuid(), profile.getName(), dto));
    }
}
