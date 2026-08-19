package com.studentos.controller;

import com.studentos.dao.TaskDAO;
import com.studentos.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ScheduleController extends HttpServlet {
    private final TaskDAO taskDAO = new TaskDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth/signin");
            return;
        }

        request.setAttribute("tasks", taskDAO.getTasksByUserId(user.getId()));
        request.getRequestDispatcher("/views/life/schedule.jsp").forward(request, response);
    }
}
