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

@WebServlet("/auth/verify")
public class VerifyOTPController extends HttpServlet {
    private AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/views/auth/verify.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        String expectedOtp = (String) session.getAttribute("otpCode");
        String enteredOtp = req.getParameter("otp");

        if (expectedOtp != null && expectedOtp.equals(enteredOtp)) {
            // OTP matches, finalize registration
            String email = (String) session.getAttribute("pendingEmail");
            String password = (String) session.getAttribute("pendingPassword");
            String firstName = (String) session.getAttribute("pendingFirstName");
            String lastName = (String) session.getAttribute("pendingLastName");
            String role = (String) session.getAttribute("pendingRole");

            User user = authService.registerUser(email, password, role, firstName, lastName);
            if (user != null) {
                session.setAttribute("user", user);
                // Clear pending session data
                session.removeAttribute("otpCode");
                session.removeAttribute("pendingEmail");
                session.removeAttribute("pendingPassword");
                
                resp.sendRedirect(req.getContextPath() + "/dashboard");
            } else {
                resp.sendRedirect(req.getContextPath() + "/auth/register.jsp?error=RegistrationFailed");
            }
        } else {
            // Invalid OTP
            req.setAttribute("error", "Invalid OTP code. Please try again.");
            req.getRequestDispatcher("/views/auth/verify.jsp").forward(req, resp);
        }
    }
}
