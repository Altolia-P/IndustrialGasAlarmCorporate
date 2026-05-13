package com.niit.industrialgasalarmcorporate.common.exception;

import com.niit.industrialgasalarmcorporate.common.enums.ErrorCode;

public class ContentNotFoundException extends BusinessException {

    private final String contentUuid;

    public ContentNotFoundException(String contentUuid) {
        super(ErrorCode.CONTENT_NOT_FOUND);
        this.contentUuid = contentUuid;
    }

    public String getContentUuid() {
        return contentUuid;
    }
}
