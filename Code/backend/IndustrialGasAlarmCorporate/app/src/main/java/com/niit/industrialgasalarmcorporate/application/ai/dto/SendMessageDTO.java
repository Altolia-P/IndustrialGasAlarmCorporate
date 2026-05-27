package com.niit.industrialgasalarmcorporate.application.ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SendMessageDTO {

    private String sessionId;

    @NotBlank(message = "消息不能为空")
    @Size(max = 500, message = "消息不能超过500字")
    private String message;
}
