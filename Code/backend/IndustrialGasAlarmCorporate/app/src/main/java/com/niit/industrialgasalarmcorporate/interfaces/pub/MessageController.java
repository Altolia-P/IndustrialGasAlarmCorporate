package com.niit.industrialgasalarmcorporate.interfaces.pub;

import com.niit.industrialgasalarmcorporate.application.message.dto.SubmitMessageDTO;
import com.niit.industrialgasalarmcorporate.application.message.service.MessageService;
import com.niit.industrialgasalarmcorporate.common.base.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/messages")
    public Result<Map<String, String>> submitMessage(@Valid @RequestBody SubmitMessageDTO dto,
                                                      HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String messageUuid = messageService.submitMessage(dto, ip);
        return Result.ok("提交成功，我们将尽快联系您", Map.of("messageUuid", messageUuid));
    }
}
