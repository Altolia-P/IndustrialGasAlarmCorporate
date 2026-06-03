package com.niit.industrialgasalarmcorporate.common.utils;

/**
 * PII 脱敏工具类 — 日志输出时防止敏感信息泄露
 */
public final class MaskUtil {

    private MaskUtil() {}

    /**
     * 脱敏邮箱：abc@example.com → a***@example.com
     */
    public static String email(String email) {
        if (email == null || email.isBlank()) return "";
        int atIdx = email.indexOf('@');
        if (atIdx <= 0) return maskGeneric(email);
        String name = email.substring(0, atIdx);
        String domain = email.substring(atIdx);
        if (name.length() <= 1) return name + "***" + domain;
        return name.charAt(0) + "***" + domain;
    }

    /**
     * 脱敏手机号：13800138000 → 138****8000
     */
    public static String phone(String phone) {
        if (phone == null || phone.isBlank()) return "";
        if (phone.length() < 7) return maskGeneric(phone);
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }

    /**
     * 脱敏接收方地址 — 自动识别 email / phone
     */
    public static String recipient(String recipient) {
        if (recipient == null || recipient.isBlank()) return "";
        if (recipient.contains("@")) return email(recipient);
        if (recipient.matches(".*\\d{7,}.*")) return phone(recipient);
        return maskGeneric(recipient);
    }

    /**
     * 脱敏通知内容 — 超过 50 字符截断
     */
    public static String content(String content) {
        if (content == null) return "";
        if (content.length() <= 50) return content;
        return content.substring(0, 50) + "...";
    }

    /**
     * 通用脱敏：字符串保留首尾各1字符，中间用 *** 替换
     */
    private static String maskGeneric(String s) {
        if (s == null || s.length() <= 2) return s;
        return s.charAt(0) + "***" + s.charAt(s.length() - 1);
    }
}
