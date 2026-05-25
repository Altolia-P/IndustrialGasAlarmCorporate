package com.niit.industrialgasalarmcorporate.interfaces.pub;

import com.niit.industrialgasalarmcorporate.application.ai.dto.SendMessageDTO;
import com.niit.industrialgasalarmcorporate.application.ai.service.AIChatService;
import com.niit.industrialgasalarmcorporate.application.ai.vo.ChatResponseVO;
import com.niit.industrialgasalarmcorporate.common.base.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class AIChatController {

    private final AIChatService aiChatService;

    @PostMapping("/ai/chat")
    public Result<ChatResponseVO> chat(@Valid @RequestBody SendMessageDTO dto,
                                       HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        return Result.ok(aiChatService.chat(dto, ip));
    }
}
