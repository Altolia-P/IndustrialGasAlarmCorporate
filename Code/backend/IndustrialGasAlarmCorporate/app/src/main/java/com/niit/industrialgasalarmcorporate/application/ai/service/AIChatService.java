package com.niit.industrialgasalarmcorporate.application.ai.service;

import com.niit.industrialgasalarmcorporate.application.ai.dto.SendMessageDTO;
import com.niit.industrialgasalarmcorporate.application.ai.vo.ChatResponseVO;

public interface AIChatService {

    ChatResponseVO chat(SendMessageDTO dto, String clientIp);
}
