package com.studentos.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

@WebFilter("/*")
public class LoggingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        System.out.println("RECEIVED REQUEST: " + request.getMethod() + " " + request.getRequestURI() + " from " + request.getRemoteAddr());
        chain.doFilter(req, res);
    }
}
