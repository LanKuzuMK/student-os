package com.studentos.controller;

import com.studentos.model.User;
import com.studentos.service.AuthService;
import com.studentos.util.AccessPolicy;
import com.studentos.util.AuthAttemptLimiter;
import com.studentos.util.CsrfUtil;
import com.studentos.util.PersistentSessionManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Dedicated endpoint for the deployed sign-in form.
 */
public class SignInController extends HttpServlet {
    private final AuthService authService = new AuthService();
    private final PersistentSessionManager persistentSessionManager = new PersistentSessionManager();
    private final AuthAttemptLimiter authAttempts = new AuthAttemptLimiter();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        CsrfUtil.getOrCreateToken(request);
        request.getRequestDispatcher("/views/auth/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!CsrfUtil.hasValidToken(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String email = request.getParameter("email");
        String remoteAddress = request.getRemoteAddr();
        if (!authAttempts.isAllowed("login", remoteAddress, email)) {
            response.sendRedirect(request.getContextPath() + "/auth/signin?error=1");
            return;
        }

        User user = authService.login(email, request.getParameter("password"));
        if (user == null) {
            authAttempts.recordFailure("login", remoteAddress, email);
            response.sendRedirect(request.getContextPath() + "/auth/signin?error=1");
            return;
        }

        authAttempts.recordSuccess("login", remoteAddress, email);
        persistentSessionManager.establish(request, response, user);
        response.sendRedirect(request.getContextPath() + AccessPolicy.postLoginPath(user.getRole()));
    }
}
