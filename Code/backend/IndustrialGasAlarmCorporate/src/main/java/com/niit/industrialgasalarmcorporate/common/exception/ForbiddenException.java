package com.niit.industrialgasalarmcorporate.common.exception;

import com.niit.industrialgasalarmcorporate.common.enums.ErrorCode;

public class ForbiddenException extends BusinessException {

    public ForbiddenException() {
        super(ErrorCode.FORBIDDEN);
    }
}
