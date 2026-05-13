package com.niit.industrialgasalarmcorporate.interfaces.pub;

import com.niit.industrialgasalarmcorporate.application.message.dto.SubmitMessageDTO;
import com.niit.industrialgasalarmcorporate.application.message.service.MessageService;
import com.niit.industrialgasalarmcorporate.common.base.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/messages")
    public Result<Void> submitMessage(@Valid @RequestBody SubmitMessageDTO dto,
                                       HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        messageService.submitMessage(dto, ip);
        return Result.ok("提交成功，我们将尽快联系您", null);
    }
}
