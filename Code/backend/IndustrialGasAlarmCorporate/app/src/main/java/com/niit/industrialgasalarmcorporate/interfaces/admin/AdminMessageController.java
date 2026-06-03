package com.niit.industrialgasalarmcorporate.interfaces.admin;

import com.niit.industrialgasalarmcorporate.application.comment.dto.CreateCommentDTO;
import com.niit.industrialgasalarmcorporate.application.comment.service.CommentService;
import com.niit.industrialgasalarmcorporate.application.comment.vo.CommentVO;
import com.niit.industrialgasalarmcorporate.application.message.dto.AssignMessageDTO;
import com.niit.industrialgasalarmcorporate.application.message.dto.BatchProcessDTO;
import com.niit.industrialgasalarmcorporate.application.message.dto.ProcessMessageDTO;
import com.niit.industrialgasalarmcorporate.application.message.service.MessageService;
import com.niit.industrialgasalarmcorporate.application.message.vo.MessageVO;
import com.niit.industrialgasalarmcorporate.common.base.Page;
import com.niit.industrialgasalarmcorporate.common.base.Result;
import com.niit.industrialgasalarmcorporate.domain.comment.CommentAuthorType;
import com.niit.industrialgasalarmcorporate.domain.comment.CommentTargetType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.niit.industrialgasalarmcorporate.infrastructure.aop.LogOperation;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminMessageController {

    private final MessageService messageService;
    private final CommentService commentService;

    @GetMapping("/messages")
    public Result<Page<MessageVO>> getMessages(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return Result.ok(messageService.findMessages(name, phone, status, page, size));
    }

    @LogOperation(operation = "ASSIGN", targetType = "MESSAGE")
    @PutMapping("/messages/{uuid}/assign")
    public Result<Void> assignMessage(@PathVariable String uuid,
                                      @Valid @RequestBody AssignMessageDTO dto) {
        messageService.assignMessage(uuid, dto);
        return Result.ok("指派成功", null);
    }

    @LogOperation(operation = "PROCESS", targetType = "MESSAGE")
    @PutMapping("/messages/{uuid}/process")
    public Result<Void> processMessage(@PathVariable String uuid,
                                        @Valid @RequestBody ProcessMessageDTO dto,
                                        @RequestAttribute String username) {
        messageService.markProcessed(uuid, dto, username);
        return Result.ok("处理成功", null);
    }

    @LogOperation(operation = "PROCESS", targetType = "MESSAGE")
    @PutMapping("/messages/process/batch")
    public Result<Void> batchProcess(@Valid @RequestBody BatchProcessDTO dto,
                                      @RequestAttribute String username) {
        messageService.batchProcess(dto, username);
        return Result.ok("批量处理成功", null);
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
        return Result.ok("评论成功", commentService.addComment(
                CommentTargetType.MESSAGE, uuid, CommentAuthorType.ADMIN, userUuid, username, dto));
    }
}
