package com.studentos.util;

/** Minimal context-aware encoders for JSP views that render user-provided values. */
public final class HtmlUtil {
    private HtmlUtil() {
    }

    public static String escapeHtml(Object value) {
        if (value == null) {
            return "";
        }
        String text = String.valueOf(value);
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }

    public static String escapeJavaScript(String value) {
        if (value == null) {
            return "";
        }
        StringBuilder encoded = new StringBuilder(value.length() + 16);
        for (int i = 0; i < value.length(); i++) {
            char character = value.charAt(i);
            switch (character) {
                case '\\' -> encoded.append("\\\\");
                case '\'' -> encoded.append("\\'");
                case '"' -> encoded.append("\\\"");
                case '\n' -> encoded.append("\\n");
                case '\r' -> encoded.append("\\r");
                case '<' -> encoded.append("\\u003C");
                case '>' -> encoded.append("\\u003E");
                case '&' -> encoded.append("\\u0026");
                case '\u2028' -> encoded.append("\\u2028");
                case '\u2029' -> encoded.append("\\u2029");
                default -> encoded.append(character);
            }
        }
        return escapeHtml(encoded.toString());
    }
}
