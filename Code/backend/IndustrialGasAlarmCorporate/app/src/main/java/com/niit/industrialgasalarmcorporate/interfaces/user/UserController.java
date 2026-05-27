package com.niit.industrialgasalarmcorporate.interfaces.user;

import com.niit.industrialgasalarmcorporate.application.message.service.MessageService;
import com.niit.industrialgasalarmcorporate.application.message.vo.MessageVO;
import com.niit.industrialgasalarmcorporate.application.workorder.service.WorkOrderService;
import com.niit.industrialgasalarmcorporate.application.workorder.vo.WorkOrderVO;
import com.niit.industrialgasalarmcorporate.common.base.Page;
import com.niit.industrialgasalarmcorporate.common.base.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final MessageService messageService;
    private final WorkOrderService workOrderService;

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
}
