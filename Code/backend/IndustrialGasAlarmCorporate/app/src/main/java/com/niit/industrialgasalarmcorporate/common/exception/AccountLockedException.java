package com.niit.industrialgasalarmcorporate.common.exception;

import com.niit.industrialgasalarmcorporate.common.enums.ErrorCode;

public class AccountLockedException extends BusinessException {

    private final String userUuid;

    public AccountLockedException(String userUuid) {
        super(ErrorCode.ACCOUNT_LOCKED);
        this.userUuid = userUuid;
    }

    public String getUserUuid() {
        return userUuid;
    }
}
