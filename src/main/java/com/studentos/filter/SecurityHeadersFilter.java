package com.studentos.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/** Adds browser-side hardening headers without changing page markup or user flows. */
@WebFilter(urlPatterns = "/*")
public class SecurityHeadersFilter implements Filter {
    private static final String CONTENT_SECURITY_POLICY = "default-src 'self'; "
            + "script-src 'self' 'unsafe-inline'; "
            + "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com; "
            + "font-src 'self' data: https://fonts.gstatic.com; "
            + "img-src 'self' data:; connect-src 'self'; object-src 'none'; "
            + "base-uri 'self'; form-action 'self'; frame-ancestors 'none'";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setHeader("Content-Security-Policy", CONTENT_SECURITY_POLICY);
        httpResponse.setHeader("X-Content-Type-Options", "nosniff");
        httpResponse.setHeader("X-Frame-Options", "DENY");
        httpResponse.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        httpResponse.setHeader("Permissions-Policy", "camera=(), geolocation=(), microphone=()");
        httpResponse.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
        if (isSensitiveResponse(httpRequest)) {
            httpResponse.setHeader("Cache-Control", "private, no-store, max-age=0");
            httpResponse.setHeader("Pragma", "no-cache");
        }
        chain.doFilter(request, response);
    }

    private boolean isSensitiveResponse(HttpServletRequest request) {
        String path = request.getRequestURI();
        return request.getSession(false) != null
                || (path != null && (path.contains("/auth/") || path.endsWith("/health")));
    }
}
