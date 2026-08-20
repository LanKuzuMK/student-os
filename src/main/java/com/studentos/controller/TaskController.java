package com.studentos.controller;

import com.studentos.dao.TaskDAO;
import com.studentos.model.Task;
import com.studentos.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@WebServlet("/tasks/*")
public class TaskController extends HttpServlet {
    private final TaskDAO taskDAO = new TaskDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth/signin");
            return;
        }

        String path = request.getPathInfo();
        if ("/create".equals(path)) {
            Task task = new Task();
            task.setUserId(user.getId());
            task.setTitle(request.getParameter("title"));
            task.setDescription(request.getParameter("description"));
            task.setPriority(request.getParameter("priority"));
            taskDAO.createTask(task);
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        int taskId;
        try {
            taskId = Integer.parseInt(request.getParameter("id"));
            if (taskId <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            redirectWithMessage(request, response, "error", "That task could not be found.");
            return;
        }

        if ("/complete".equals(path)) {
            boolean changed = taskDAO.updateTaskStatus(taskId, user.getId(), "COMPLETED");
            redirectWithMessage(request, response, changed ? "success" : "error", changed ? "Task marked complete." : "We could not complete that task.");
        } else if ("/delete".equals(path)) {
            boolean deleted = taskDAO.deleteCompletedTask(taskId, user.getId());
            redirectWithMessage(request, response, deleted ? "success" : "error", deleted ? "Completed task deleted." : "Only your completed tasks can be deleted.");
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void redirectWithMessage(HttpServletRequest request, HttpServletResponse response, String type, String message) throws IOException {
        String destination = "schedule".equals(request.getParameter("returnTo")) ? "/schedule" : "/dashboard";
        response.sendRedirect(request.getContextPath() + destination + "?" + type + "=" + URLEncoder.encode(message, StandardCharsets.UTF_8));
    }
}
