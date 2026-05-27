package com.niit.industrialgasalarmcorporate.common.base;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> {

    private final int code;
    private final String message;
    private final T data;
    private final boolean success;

    private Result(int code, String message, T data, boolean success) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.success = success;
    }

    public static <T> Result<T> ok(T data) {
        return new Result<>(0, "成功", data, true);
    }

    public static <T> Result<T> ok(String message, T data) {
        return new Result<>(0, message, data, true);
    }

    public static <T> Result<T> fail(int code, String message) {
        return new Result<>(code, message, null, false);
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public boolean isSuccess() {
        return success;
    }
}
