package com.studentos.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

/** Adds an opaque request reference to logs and error pages without recording form or query content. */
public class RequestTraceFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String traceId = UUID.randomUUID().toString().substring(0, 12);
        long startedAt = System.nanoTime();
        request.setAttribute("requestTraceId", traceId);
        try {
            chain.doFilter(request, response);
            log(request, traceId, startedAt, "completed");
        } catch (IOException | ServletException exception) {
            log(request, traceId, startedAt, "failed:" + exception.getClass().getSimpleName());
            throw exception;
        }
    }

    private void log(ServletRequest request, String traceId, long startedAt, String outcome) {
        String method = request instanceof HttpServletRequest http ? http.getMethod() : "REQUEST";
        String path = request instanceof HttpServletRequest http ? http.getRequestURI() : "/";
        long durationMs = (System.nanoTime() - startedAt) / 1_000_000L;
        System.out.println("studentos_request trace=" + traceId + " method=" + method + " path=" + path + " duration_ms=" + durationMs + " outcome=" + outcome);
    }
}
