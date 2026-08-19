package com.studentos.controller;

import com.studentos.model.User;
import com.studentos.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Random;

/**
 * Handles the application's explicit authentication endpoints.
 * Exact mappings avoid relying on a wildcard-path resolution in the deployed container.
 */
@WebServlet(
        name = "authController",
        urlPatterns = {
                "/auth/login",
                "/auth/signin",
                "/auth/register",
                "/auth/verify",
                "/auth/logout"
        }
)
public class AuthController extends HttpServlet {
    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String route = getRoute(request);

        switch (route) {
            case "/auth/login", "/auth/signin" ->
                    request.getRequestDispatcher("/views/auth/login.jsp").forward(request, response);
            case "/auth/register" ->
                    request.getRequestDispatcher("/views/auth/register.jsp").forward(request, response);
            case "/auth/verify" ->
                    request.getRequestDispatcher("/views/auth/verify.jsp").forward(request, response);
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
            case "/auth/login", "/auth/signin" -> handleLogin(request, response);
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
            request.getSession().setAttribute("user", user);
            response.sendRedirect(request.getContextPath() + ("ADMIN".equals(user.getRole()) ? "/admin" : "/dashboard"));
        } else {
            response.sendRedirect(request.getContextPath() + "/auth/signin?error=1");
        }
    }

    private void handleRegistrationRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        session.setAttribute("otpCode", String.format("%06d", new Random().nextInt(1_000_000)));
        session.setAttribute("pendingEmail", request.getParameter("email"));
        session.setAttribute("pendingPassword", request.getParameter("password"));
        session.setAttribute("pendingFirstName", request.getParameter("firstName"));
        session.setAttribute("pendingLastName", request.getParameter("lastName"));
        session.setAttribute("pendingRole", request.getParameter("role") != null ? request.getParameter("role") : "STUDENT");
        response.sendRedirect(request.getContextPath() + "/auth/verify");
    }

    private void handleVerification(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String expectedOtp = (String) session.getAttribute("otpCode");
        String enteredOtp = request.getParameter("otp");

        if (expectedOtp != null && expectedOtp.equals(enteredOtp)) {
            User user = authService.registerUser(
                    (String) session.getAttribute("pendingEmail"),
                    (String) session.getAttribute("pendingPassword"),
                    (String) session.getAttribute("pendingRole"),
                    (String) session.getAttribute("pendingFirstName"),
                    (String) session.getAttribute("pendingLastName")
            );

            if (user != null) {
                session.setAttribute("user", user);
                session.removeAttribute("otpCode");
                session.removeAttribute("pendingEmail");
                session.removeAttribute("pendingPassword");
                response.sendRedirect(request.getContextPath() + "/dashboard");
            } else {
                response.sendRedirect(request.getContextPath() + "/auth/register?error=RegistrationFailed");
            }
        } else {
            request.setAttribute("error", "Invalid OTP code. Please try again.");
            request.getRequestDispatcher("/views/auth/verify.jsp").forward(request, response);
        }
    }
}
