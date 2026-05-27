package com.niit.industrialgasalarmcorporate.common.enums;

public enum ErrorCode {

    SUCCESS(0, "成功"),
    VALIDATION_ERROR(4000, "参数校验失败"),
    PRODUCT_NOT_FOUND(4001, "产品不存在"),
    CONTENT_NOT_FOUND(4002, "内容不存在"),
    CATEGORY_NOT_FOUND(4003, "分类不存在"),
    MESSAGE_NOT_FOUND(4004, "留言不存在"),
    USER_NOT_FOUND(4005, "管理员不存在"),
    ACCOUNT_LOCKED(4006, "账户已锁定"),
    INVALID_PASSWORD(4007, "密码错误"),
    UNAUTHORIZED(4008, "未登录"),
    FORBIDDEN(4009, "无权限"),
    STAFF_NOT_FOUND(4010, "员工不存在"),
    WORK_ORDER_NOT_FOUND(4011, "工单不存在"),
    DEVICE_NOT_FOUND(4012, "设备不存在"),
    DEVICE_SERIAL_DUPLICATE(4013, "设备序列号已存在"),
    ALERT_RULE_NOT_FOUND(4014, "报警规则不存在"),
    ALERT_NOT_FOUND(4015, "报警记录不存在"),
    NOTIFICATION_NOT_FOUND(4016, "通知记录不存在"),
    SYSTEM_ERROR(5001, "系统内部错误");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
