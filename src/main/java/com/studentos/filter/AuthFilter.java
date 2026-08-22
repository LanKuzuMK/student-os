package com.studentos.filter;

import com.studentos.dao.UserDAO;
import com.studentos.model.User;
import com.studentos.util.CsrfUtil;
import com.studentos.util.PersistentSessionManager;
import com.studentos.util.SessionVersionUtil;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(urlPatterns = {"/dashboard", "/dashboard/*", "/schedule", "/goals", "/tasks", "/tasks/*", "/skills", "/skills/*", "/freelance", "/freelance/*", "/messages", "/messages/*", "/profile", "/profile/*", "/account", "/account/*", "/reports", "/reports/*", "/notifications", "/notifications/*", "/collaborations", "/collaborations/*", "/projects", "/projects/*", "/saved", "/saved/*", "/admin", "/admin/*"})
public class AuthFilter implements Filter {
    private final UserDAO userDAO = new UserDAO();
    private final PersistentSessionManager persistentSessionManager = new PersistentSessionManager();

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        HttpSession session = request.getSession(false);
        
        User sessionUser = session == null ? null : (User) session.getAttribute("user");
        if (sessionUser == null) {
            sessionUser = persistentSessionManager.restore(request, response);
            session = request.getSession(false);
        }
        User currentUser = sessionUser == null ? null : userDAO.findById(sessionUser.getId());
        Integer sessionVersion = session == null ? null : (Integer) session.getAttribute("authVersion");
        if (currentUser == null || !"ACTIVE".equals(currentUser.getStatus())
                || !SessionVersionUtil.isCurrent(sessionVersion, currentUser.getAuthVersion())) {
            if (session != null) {
                session.invalidate();
            }
            persistentSessionManager.revokeCurrent(request, response);
            response.sendRedirect(request.getContextPath() + "/auth/signin");
            return;
        }

        session.setAttribute("user", currentUser);
        session.setAttribute("authVersion", currentUser.getAuthVersion());
        persistentSessionManager.refreshIfPresent(request, response);
        if ("POST".equalsIgnoreCase(request.getMethod()) && !CsrfUtil.hasValidToken(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        CsrfUtil.getOrCreateToken(request);
        chain.doFilter(request, response);
    }
}
