package com.niit.industrialgasalarmcorporate.common.exception;

import com.niit.industrialgasalarmcorporate.common.enums.ErrorCode;

public class MessageNotFoundException extends BusinessException {

    private final String messageUuid;

    public MessageNotFoundException(String messageUuid) {
        super(ErrorCode.MESSAGE_NOT_FOUND);
        this.messageUuid = messageUuid;
    }

    public String getMessageUuid() {
        return messageUuid;
    }
}
