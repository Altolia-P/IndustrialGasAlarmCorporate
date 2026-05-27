package com.niit.industrialgasalarmcorporate.common.exception;

import com.niit.industrialgasalarmcorporate.common.enums.ErrorCode;

public class UserNotFoundException extends BusinessException {

    private final String username;

    public UserNotFoundException(String username) {
        super(ErrorCode.USER_NOT_FOUND);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
