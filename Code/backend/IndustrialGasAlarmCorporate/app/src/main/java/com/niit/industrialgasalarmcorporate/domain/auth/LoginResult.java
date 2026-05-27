package com.niit.industrialgasalarmcorporate.domain.auth;

public class LoginResult {

    private final boolean success;
    private final String reason;

    private LoginResult(boolean success, String reason) {
        this.success = success;
        this.reason = reason;
    }

    public static LoginResult success() {
        return new LoginResult(true, null);
    }

    public static LoginResult failed(String reason) {
        return new LoginResult(false, reason);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getReason() {
        return reason;
    }
}
