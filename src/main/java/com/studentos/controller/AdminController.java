package com.studentos.controller;
import com.studentos.dao.UserDAO;
import com.studentos.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/admin/*")
public class AdminController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null || !"ADMIN".equals(user.getRole())) {
            response.sendError(403);
            return;
        }
        // Dummy stats for the admin dashboard
        request.setAttribute("totalUsers", 1248);
        request.setAttribute("totalSkills", 312);
        request.setAttribute("totalJobs", 96);
        request.getRequestDispatcher("/views/admin/index.jsp").forward(request, response);
    }
}
