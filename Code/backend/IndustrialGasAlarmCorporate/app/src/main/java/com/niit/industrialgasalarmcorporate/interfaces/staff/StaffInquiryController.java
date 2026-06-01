package com.niit.industrialgasalarmcorporate.interfaces.staff;

import com.niit.industrialgasalarmcorporate.application.comment.dto.CreateCommentDTO;
import com.niit.industrialgasalarmcorporate.application.comment.service.CommentService;
import com.niit.industrialgasalarmcorporate.application.comment.vo.CommentVO;
import com.niit.industrialgasalarmcorporate.application.message.dto.ProcessMessageDTO;
import com.niit.industrialgasalarmcorporate.application.message.service.MessageService;
import com.niit.industrialgasalarmcorporate.application.message.vo.MessageVO;
import com.niit.industrialgasalarmcorporate.application.staff.service.StaffService;
import com.niit.industrialgasalarmcorporate.application.staff.vo.StaffVO;
import com.niit.industrialgasalarmcorporate.common.base.Page;
import com.niit.industrialgasalarmcorporate.common.base.Result;
import com.niit.industrialgasalarmcorporate.domain.comment.CommentAuthorType;
import com.niit.industrialgasalarmcorporate.domain.comment.CommentTargetType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/staff")
@RequiredArgsConstructor
public class StaffInquiryController {

    private final MessageService messageService;
    private final StaffService staffService;
    private final CommentService commentService;

    @GetMapping("/inquiries")
    public Result<Page<MessageVO>> getMyInquiries(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestAttribute String userUuid) {
        StaffVO profile = staffService.getMyProfile(userUuid);
        return Result.ok(messageService.findStaffMessages(profile.getStaffUuid(), page, size));
    }

    @GetMapping("/inquiries/{uuid}/comments")
    public Result<List<CommentVO>> getInquiryComments(@PathVariable String uuid) {
        return Result.ok(commentService.findByTarget(CommentTargetType.MESSAGE, uuid));
    }

    @PutMapping("/inquiries/{uuid}/process")
    public Result<Void> processMyInquiry(@PathVariable String uuid,
                                         @Valid @RequestBody ProcessMessageDTO dto,
                                         @RequestAttribute String userUuid) {
        StaffVO profile = staffService.getMyProfile(userUuid);
        messageService.markMyInquiryProcessed(profile.getStaffUuid(), uuid, dto, profile.getName());
        return Result.ok("处理完成", null);
    }

    @PostMapping("/inquiries/{uuid}/comments")
    public Result<CommentVO> addInquiryComment(@PathVariable String uuid,
                                                @Valid @RequestBody CreateCommentDTO dto,
                                                @RequestAttribute String userUuid) {
        StaffVO profile = staffService.getMyProfile(userUuid);
        messageService.getMyInquiryDetail(profile.getStaffUuid(), uuid);
        return Result.ok("评论成功", commentService.addComment(
                CommentTargetType.MESSAGE, uuid,
                CommentAuthorType.STAFF, profile.getStaffUuid(), profile.getName(), dto));
    }
}
