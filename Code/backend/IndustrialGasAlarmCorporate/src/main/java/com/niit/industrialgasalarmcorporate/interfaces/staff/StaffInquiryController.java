package com.niit.industrialgasalarmcorporate.interfaces.staff;

import com.niit.industrialgasalarmcorporate.application.message.service.MessageService;
import com.niit.industrialgasalarmcorporate.application.message.vo.MessageVO;
import com.niit.industrialgasalarmcorporate.common.base.Page;
import com.niit.industrialgasalarmcorporate.common.base.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/staff")
@RequiredArgsConstructor
public class StaffInquiryController {

    private final MessageService messageService;

    @GetMapping("/inquiries")
    public Result<Page<MessageVO>> getMyInquiries(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestAttribute String userUuid) {
        return Result.ok(messageService.findStaffMessages(userUuid, page, size));
    }
}
