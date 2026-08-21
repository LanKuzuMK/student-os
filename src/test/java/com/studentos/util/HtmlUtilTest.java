package com.studentos.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HtmlUtilTest {
    @Test
    void escapesHtmlSensitiveCharacters() {
        assertEquals("&lt;img src=&quot;x&quot; onerror=&#x27;bad&#x27;&gt;", HtmlUtil.escapeHtml("<img src=\"x\" onerror='bad'>"));
    }

    @Test
    void escapesJavaScriptStringBoundaryCharacters() {
        assertEquals("\\&#x27;;alert(1)//", HtmlUtil.escapeJavaScript("';alert(1)//"));
    }
}
