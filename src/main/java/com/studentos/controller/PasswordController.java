package com.studentos.controller;

import com.studentos.model.User;
import com.studentos.service.AuthService;
import com.studentos.util.CsrfUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/** Handles password changes for authenticated accounts. */
public class PasswordController extends HttpServlet {
    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (signedInUser(request) == null) {
            response.sendRedirect(request.getContextPath() + "/auth/signin");
            return;
        }
        CsrfUtil.getOrCreateToken(request);
        request.getRequestDispatcher("/views/auth/password.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = signedInUser(request);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth/signin");
            return;
        }
        String newPassword = request.getParameter("newPassword");
        if (newPassword == null || !newPassword.equals(request.getParameter("confirmPassword"))) {
            response.sendRedirect(request.getContextPath() + "/account/password?error=mismatch");
            return;
        }
        if (!authService.changePassword(user, request.getParameter("currentPassword"), newPassword)) {
            response.sendRedirect(request.getContextPath() + "/account/password?error=invalid");
            return;
        }
        request.changeSessionId();
        response.sendRedirect(request.getContextPath() + "/account/password?success=1");
    }

    private User signedInUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session == null ? null : (User) session.getAttribute("user");
    }
}
