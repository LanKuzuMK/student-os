package com.studentos.controller;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import com.studentos.dao.TaskDAO;
import com.studentos.model.User;

@WebServlet("/dashboard")
public class DashboardController extends HttpServlet {
    private TaskDAO taskDAO = new TaskDAO();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }
        request.setAttribute("tasks", taskDAO.getTasksByUserId(user.getId()));
        request.getRequestDispatcher("/views/dashboard/index.jsp").forward(request, response);
    }
}
