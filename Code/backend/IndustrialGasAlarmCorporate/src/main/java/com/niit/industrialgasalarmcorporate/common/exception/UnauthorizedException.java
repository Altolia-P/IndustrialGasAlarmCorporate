package com.niit.industrialgasalarmcorporate.common.exception;

import com.niit.industrialgasalarmcorporate.common.enums.ErrorCode;

public class UnauthorizedException extends BusinessException {

    public UnauthorizedException() {
        super(ErrorCode.UNAUTHORIZED);
    }
}
