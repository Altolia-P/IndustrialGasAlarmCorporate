package com.niit.industrialgasalarmcorporate.interfaces.user;

import com.niit.industrialgasalarmcorporate.application.auth.dto.UpdateProfileDTO;
import com.niit.industrialgasalarmcorporate.application.auth.dto.UserResetPasswordDTO;
import com.niit.industrialgasalarmcorporate.application.auth.service.AuthService;
import com.niit.industrialgasalarmcorporate.application.auth.vo.UserVO;
import com.niit.industrialgasalarmcorporate.application.comment.dto.CreateCommentDTO;
import com.niit.industrialgasalarmcorporate.application.comment.service.CommentService;
import com.niit.industrialgasalarmcorporate.application.comment.vo.CommentVO;
import com.niit.industrialgasalarmcorporate.application.message.service.MessageService;
import com.niit.industrialgasalarmcorporate.application.message.vo.MessageVO;
import com.niit.industrialgasalarmcorporate.application.workorder.dto.CreateWorkOrderDTO;
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
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final MessageService messageService;
    private final WorkOrderService workOrderService;
    private final AuthService authService;
    private final CommentService commentService;

    @GetMapping("/me")
    public Result<UserVO> currentUser(@RequestAttribute String userUuid) {
        return Result.ok(authService.getCurrentUser(userUuid));
    }

    @GetMapping("/messages")
    public Result<Page<MessageVO>> getMyMessages(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestAttribute String username) {
        return Result.ok(messageService.findUserMessages(username, page, size));
    }

    @GetMapping("/workorders")
    public Result<Page<WorkOrderVO>> getMyWorkOrders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestAttribute String username) {
        return Result.ok(workOrderService.findUserWorkOrders(username, page, size));
    }

    @PostMapping("/workorders")
    public Result<WorkOrderVO> createWorkOrder(@Valid @RequestBody CreateWorkOrderDTO dto) {
        return Result.ok("提交成功", workOrderService.createWorkOrder(dto));
    }

    @PostMapping("/resetPassword")
    public Result<Void> resetPassword(@Valid @RequestBody UserResetPasswordDTO dto,
                                      @RequestAttribute String userUuid) {
        authService.resetMyPassword(userUuid, dto);
        return Result.ok("密码修改成功", null);
    }

    @PutMapping("/profile")
    public Result<UserVO> updateProfile(@Valid @RequestBody UpdateProfileDTO dto,
                                        @RequestAttribute String userUuid) {
        return Result.ok("资料更新成功", authService.updateProfile(userUuid, dto));
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
        WorkOrderVO wo = workOrderService.getWorkOrder(uuid);
        if (!username.equals(wo.getCustomerName())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "只能评论自己的工单");
        }
        return Result.ok("评论成功", commentService.addComment(
                CommentTargetType.WORK_ORDER, uuid, CommentAuthorType.USER, userUuid, username, dto));
    }

    @GetMapping("/messages/{uuid}/comments")
    public Result<List<CommentVO>> getMessageComments(@PathVariable String uuid) {
        return Result.ok(commentService.findByTarget(CommentTargetType.MESSAGE, uuid));
    }

    @PostMapping("/messages/{uuid}/comments")
    public Result<CommentVO> addMessageComment(@PathVariable String uuid,
                                                @Valid @RequestBody CreateCommentDTO dto,
                                                @RequestAttribute String userUuid,
                                                @RequestAttribute String username) {
        MessageVO msg = messageService.getMessage(uuid);
        if (!username.equals(msg.getName())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "只能评论自己的咨询");
        }
        return Result.ok("评论成功", commentService.addComment(
                CommentTargetType.MESSAGE, uuid, CommentAuthorType.USER, userUuid, username, dto));
    }
}
