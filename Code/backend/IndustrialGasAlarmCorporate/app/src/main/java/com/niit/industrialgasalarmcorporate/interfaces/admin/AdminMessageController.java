package com.niit.industrialgasalarmcorporate.interfaces.admin;

import com.niit.industrialgasalarmcorporate.application.message.dto.AssignMessageDTO;
import com.niit.industrialgasalarmcorporate.application.message.dto.BatchProcessDTO;
import com.niit.industrialgasalarmcorporate.application.message.dto.ProcessMessageDTO;
import com.niit.industrialgasalarmcorporate.application.message.service.MessageService;
import com.niit.industrialgasalarmcorporate.application.message.vo.MessageVO;
import com.niit.industrialgasalarmcorporate.common.base.Page;
import com.niit.industrialgasalarmcorporate.common.base.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminMessageController {

    private final MessageService messageService;

    @GetMapping("/messages")
    public Result<Page<MessageVO>> getMessages(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return Result.ok(messageService.findMessages(name, phone, status, page, size));
    }

    @PutMapping("/messages/{uuid}/assign")
    public Result<Void> assignMessage(@PathVariable String uuid,
                                      @Valid @RequestBody AssignMessageDTO dto) {
        messageService.assignMessage(uuid, dto);
        return Result.ok("指派成功", null);
    }

    @PutMapping("/messages/{uuid}/process")
    public Result<Void> processMessage(@PathVariable String uuid,
                                        @Valid @RequestBody ProcessMessageDTO dto,
                                        @RequestAttribute String username) {
        messageService.markProcessed(uuid, dto, username);
        return Result.ok("处理成功", null);
    }

    @PutMapping("/messages/process/batch")
    public Result<Void> batchProcess(@Valid @RequestBody BatchProcessDTO dto,
                                      @RequestAttribute String username) {
        messageService.batchProcess(dto, username);
        return Result.ok("批量处理成功", null);
    }
}
