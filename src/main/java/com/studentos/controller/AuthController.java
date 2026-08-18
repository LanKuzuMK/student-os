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
        if ("/login".equals(path)) {
            req.getRequestDispatcher("/views/auth/login.jsp").forward(req, resp);
        } else if ("/register".equals(path)) {
            req.getRequestDispatcher("/views/auth/register.jsp").forward(req, resp);
        } else if ("/logout".equals(path)) {
            req.getSession().invalidate();
            resp.sendRedirect(req.getContextPath() + "/auth/login");
        } else {
            resp.sendError(404);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        
        if ("/login".equals(path)) {
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
        } else if ("/register".equals(path)) {
            // Initiate OTP Flow
            String email = req.getParameter("email");
            String password = req.getParameter("password");
            String firstName = req.getParameter("firstName");
            String lastName = req.getParameter("lastName");
            String role = req.getParameter("role") != null ? req.getParameter("role") : "STUDENT";
            
            // Generate 6-digit OTP
            String otpCode = String.format("%06d", new Random().nextInt(999999));
            
            // Store temporarily in session
            HttpSession session = req.getSession();
            session.setAttribute("otpCode", otpCode);
            session.setAttribute("pendingEmail", email);
            session.setAttribute("pendingPassword", password);
            session.setAttribute("pendingFirstName", firstName);
            session.setAttribute("pendingLastName", lastName);
            session.setAttribute("pendingRole", role);
            
            // Redirect to verify page
            resp.sendRedirect(req.getContextPath() + "/auth/verify");
        } else if ("/logout".equals(path)) {
            req.getSession().invalidate();
            resp.sendRedirect(req.getContextPath() + "/views/auth/login.jsp");
        }
    }
}
