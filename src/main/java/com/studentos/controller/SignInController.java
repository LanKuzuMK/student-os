package com.studentos.controller;

import com.studentos.model.User;
import com.studentos.service.AuthService;
import com.studentos.util.AccessPolicy;
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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/views/auth/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = authService.login(request.getParameter("email"), request.getParameter("password"));
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth/signin?error=1");
            return;
        }

        persistentSessionManager.establish(request, response, user);
        response.sendRedirect(request.getContextPath() + AccessPolicy.postLoginPath(user.getRole()));
    }
}
