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

@WebServlet("/auth/*")
public class AuthController extends HttpServlet {
    private AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        if (path == null) path = "";
        path = path.trim().replaceAll("[\u200B-\u200D\uFEFF]", "");
        
        if (path.startsWith("/login") || path.startsWith("/signin")) {
            req.getRequestDispatcher("/views/auth/login.jsp").forward(req, resp);
        } else if (path.startsWith("/register")) {
            req.getRequestDispatcher("/views/auth/register.jsp").forward(req, resp);
        } else if (path.startsWith("/verify")) {
            req.getRequestDispatcher("/views/auth/verify.jsp").forward(req, resp);
        } else if (path.startsWith("/logout")) {
            req.getSession().invalidate();
            resp.sendRedirect(req.getContextPath() + "/auth/signin");
        } else {
            resp.sendError(404, "Path not found: " + path);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        if (path == null) path = "";
        path = path.trim().replaceAll("[\u200B-\u200D\uFEFF]", "");
        
        if (path.startsWith("/login") || path.startsWith("/signin")) {
            User user = authService.login(req.getParameter("email"), req.getParameter("password"));
            if (user != null) {
                req.getSession().setAttribute("user", user);
                if ("ADMIN".equals(user.getRole())) {
                    resp.sendRedirect(req.getContextPath() + "/admin");
                } else {
                    resp.sendRedirect(req.getContextPath() + "/dashboard");
                }
            } else {
                resp.sendRedirect(req.getContextPath() + "/views/auth/login.jsp?error=1");
            }
        } else if (path.startsWith("/register")) {
            String email = req.getParameter("email");
            String password = req.getParameter("password");
            String firstName = req.getParameter("firstName");
            String lastName = req.getParameter("lastName");
            String role = req.getParameter("role") != null ? req.getParameter("role") : "STUDENT";
            
            String otpCode = String.format("%06d", new Random().nextInt(999999));
            
            HttpSession session = req.getSession();
            session.setAttribute("otpCode", otpCode);
            session.setAttribute("pendingEmail", email);
            session.setAttribute("pendingPassword", password);
            session.setAttribute("pendingFirstName", firstName);
            session.setAttribute("pendingLastName", lastName);
            session.setAttribute("pendingRole", role);
            
            resp.sendRedirect(req.getContextPath() + "/auth/verify");
        } else if (path.startsWith("/verify")) {
            HttpSession session = req.getSession();
            String expectedOtp = (String) session.getAttribute("otpCode");
            String enteredOtp = req.getParameter("otp");

            if (expectedOtp != null && expectedOtp.equals(enteredOtp)) {
                String email = (String) session.getAttribute("pendingEmail");
                String password = (String) session.getAttribute("pendingPassword");
                String firstName = (String) session.getAttribute("pendingFirstName");
                String lastName = (String) session.getAttribute("pendingLastName");
                String role = (String) session.getAttribute("pendingRole");

                User user = authService.registerUser(email, password, role, firstName, lastName);
                if (user != null) {
                    session.setAttribute("user", user);
                    session.removeAttribute("otpCode");
                    session.removeAttribute("pendingEmail");
                    session.removeAttribute("pendingPassword");
                    
                    resp.sendRedirect(req.getContextPath() + "/dashboard");
                } else {
                    resp.sendRedirect(req.getContextPath() + "/views/auth/register.jsp?error=RegistrationFailed");
                }
            } else {
                req.setAttribute("error", "Invalid OTP code. Please try again.");
                req.getRequestDispatcher("/views/auth/verify.jsp").forward(req, resp);
            }
        } else if (path.startsWith("/logout")) {
            req.getSession().invalidate();
            resp.sendRedirect(req.getContextPath() + "/views/auth/signin");
        }
    }
}
