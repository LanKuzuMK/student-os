package com.studentos.filter;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(urlPatterns = {"/dashboard/*", "/tasks/*", "/skills/*", "/freelance/*", "/messages/*", "/profile/*", "/admin/*"})
public class AuthFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        HttpSession session = request.getSession(false);
        
        boolean loggedIn = session != null && session.getAttribute("user") != null;
        if (loggedIn) {
            chain.doFilter(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/auth/signin");
        }
    }
}
