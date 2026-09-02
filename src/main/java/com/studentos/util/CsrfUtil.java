package com.studentos.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/** Session-bound CSRF token support for authenticated state-changing requests. */
public final class CsrfUtil {
    public static final String REQUEST_ATTRIBUTE = "csrfToken";
    private static final String SESSION_ATTRIBUTE = "csrfToken";
    private static final SecureRandom RANDOM = new SecureRandom();

    private CsrfUtil() {
    }

    public static String getOrCreateToken(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String token = (String) session.getAttribute(SESSION_ATTRIBUTE);
        if (token == null) {
            byte[] bytes = new byte[32];
            RANDOM.nextBytes(bytes);
            token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
            session.setAttribute(SESSION_ATTRIBUTE, token);
        }
        request.setAttribute(REQUEST_ATTRIBUTE, token);
        return token;
    }

    public static boolean hasValidToken(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String expected = session == null ? null : (String) session.getAttribute(SESSION_ATTRIBUTE);
        
        String submitted = null;
        String contentType = request.getContentType();
        boolean isMultipart = contentType != null && contentType.toLowerCase().startsWith("multipart/form-data");
        
        if (!isMultipart) {
            try {
                submitted = request.getParameter(REQUEST_ATTRIBUTE);
            } catch (Exception e) {
                // Ignore parse exceptions
            }
        }
        
        if (submitted == null && request.getQueryString() != null) {
            for (String param : request.getQueryString().split("&")) {
                if (param.startsWith(REQUEST_ATTRIBUTE + "=")) {
                    submitted = param.substring(REQUEST_ATTRIBUTE.length() + 1);
                    break;
                }
            }
        }
        
        return expected != null && submitted != null && MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8), submitted.getBytes(StandardCharsets.UTF_8));
    }
}
