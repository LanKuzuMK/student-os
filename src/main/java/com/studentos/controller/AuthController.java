package com.studentos.controller;

import com.studentos.model.User;
import com.studentos.service.AuthService;
import com.studentos.util.InputValidator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Handles the application's explicit authentication endpoints.
 * Exact mappings avoid relying on a wildcard-path resolution in the deployed container.
 */
@WebServlet(
        name = "authController",
        urlPatterns = {
                "/auth/login",
                "/auth/register",
                "/auth/verify",
                "/auth/logout"
        }
)
public class AuthController extends HttpServlet {
    private final AuthService authService = new AuthService();
    private static final String PENDING_EMAIL = "pendingEmail";
    private static final String PENDING_PASSWORD = "pendingPassword";
    private static final String PENDING_FIRST_NAME = "pendingFirstName";
    private static final String PENDING_LAST_NAME = "pendingLastName";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String route = getRoute(request);

        switch (route) {
            case "/auth/login" ->
                    request.getRequestDispatcher("/views/auth/login.jsp").forward(request, response);
            case "/auth/register" ->
                    request.getRequestDispatcher("/views/auth/register.jsp").forward(request, response);
            case "/auth/verify" -> request.getRequestDispatcher("/views/auth/verify.jsp").forward(request, response);
            case "/auth/logout" -> {
                request.getSession().invalidate();
                response.sendRedirect(request.getContextPath() + "/auth/signin");
            }
            default -> response.sendError(HttpServletResponse.SC_NOT_FOUND, "Authentication route not found");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String route = getRoute(request);

        switch (route) {
            case "/auth/login" -> handleLogin(request, response);
            case "/auth/register" -> handleRegistrationRequest(request, response);
            case "/auth/verify" -> handleVerification(request, response);
            case "/auth/logout" -> {
                request.getSession().invalidate();
                response.sendRedirect(request.getContextPath() + "/auth/signin");
            }
            default -> response.sendError(HttpServletResponse.SC_NOT_FOUND, "Authentication route not found");
        }
    }

    private String getRoute(HttpServletRequest request) {
        String route = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (route != null && contextPath != null && route.startsWith(contextPath)) {
            route = route.substring(contextPath.length());
        }
        return route == null ? "" : route.trim().replaceAll("[\\u200B-\\u200D\\uFEFF]", "");
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = authService.login(request.getParameter("email"), request.getParameter("password"));
        if (user != null) {
            request.getSession();
            request.changeSessionId();
            request.getSession().setAttribute("user", user);
            response.sendRedirect(request.getContextPath() + ("ADMIN".equals(user.getRole()) ? "/admin" : "/dashboard"));
        } else {
            response.sendRedirect(request.getContextPath() + "/auth/signin?error=1");
        }
    }

    private void handleRegistrationRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        if (!InputValidator.isValidEmail(email) || password == null || password.length() < 8) {
            response.sendRedirect(request.getContextPath() + "/auth/register?error=invalid");
            return;
        }

        String normalizedEmail = email.trim().toLowerCase();
        if (!authService.startEmailVerification(normalizedEmail)) {
            response.sendRedirect(request.getContextPath() + "/auth/register?error=delivery");
            return;
        }
        HttpSession session = request.getSession();
        session.setAttribute(PENDING_EMAIL, normalizedEmail);
        session.setAttribute(PENDING_PASSWORD, password);
        session.setAttribute(PENDING_FIRST_NAME, request.getParameter("firstName"));
        session.setAttribute(PENDING_LAST_NAME, request.getParameter("lastName"));
        response.sendRedirect(request.getContextPath() + "/auth/verify");
    }

    private void handleVerification(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(PENDING_EMAIL) == null || session.getAttribute(PENDING_PASSWORD) == null) {
            response.sendRedirect(request.getContextPath() + "/auth/register?error=expired");
            return;
        }
        String email = (String) session.getAttribute(PENDING_EMAIL);
        String code = request.getParameter("otp");
        if (!authService.verifyEmailCode(email, code)) {
            response.sendRedirect(request.getContextPath() + "/auth/verify?error=invalid");
            return;
        }

        User user = authService.registerUser(
                email,
                (String) session.getAttribute(PENDING_PASSWORD),
                (String) session.getAttribute(PENDING_FIRST_NAME),
                (String) session.getAttribute(PENDING_LAST_NAME)
        );
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth/register?error=RegistrationFailed");
            return;
        }
        request.changeSessionId();
        HttpSession authenticatedSession = request.getSession();
        authenticatedSession.setAttribute("user", user);
        authenticatedSession.removeAttribute(PENDING_EMAIL);
        authenticatedSession.removeAttribute(PENDING_PASSWORD);
        authenticatedSession.removeAttribute(PENDING_FIRST_NAME);
        authenticatedSession.removeAttribute(PENDING_LAST_NAME);
        response.sendRedirect(request.getContextPath() + "/dashboard");
    }

}
