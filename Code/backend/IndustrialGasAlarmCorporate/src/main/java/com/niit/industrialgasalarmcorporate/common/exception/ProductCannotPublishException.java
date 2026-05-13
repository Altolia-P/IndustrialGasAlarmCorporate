package com.niit.industrialgasalarmcorporate.common.exception;

import com.niit.industrialgasalarmcorporate.common.enums.ErrorCode;

public class ProductCannotPublishException extends BusinessException {

    private final String reason;

    public ProductCannotPublishException(String reason) {
        super(ErrorCode.VALIDATION_ERROR, reason);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
