package com.studentos.controller;
import com.studentos.dao.TaskDAO;
import com.studentos.model.Task;
import com.studentos.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/tasks/*")
public class TaskController extends HttpServlet {
    private TaskDAO taskDAO = new TaskDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) { response.sendError(403); return; }
        
        String path = request.getPathInfo();
        if ("/create".equals(path)) {
            Task task = new Task();
            task.setUserId(user.getId());
            task.setTitle(request.getParameter("title"));
            task.setDescription(request.getParameter("description"));
            task.setPriority(request.getParameter("priority"));
            taskDAO.createTask(task);
            response.sendRedirect(request.getContextPath() + "/dashboard");
        } else if ("/complete".equals(path)) {
            int taskId = Integer.parseInt(request.getParameter("id"));
            taskDAO.updateTaskStatus(taskId, "COMPLETED");
            response.sendRedirect(request.getContextPath() + "/dashboard");
        }
    }
}
