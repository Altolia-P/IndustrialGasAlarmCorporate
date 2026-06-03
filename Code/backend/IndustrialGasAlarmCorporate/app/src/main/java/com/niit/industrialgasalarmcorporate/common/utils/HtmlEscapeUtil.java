package com.niit.industrialgasalarmcorporate.common.utils;

public final class HtmlEscapeUtil {

    private HtmlEscapeUtil() {}

    public static String escape(String input) {
        if (input == null) return null;
        return input
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
