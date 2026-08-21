package com.studentos.controller;
import com.studentos.dao.JobDAO;
import com.studentos.model.Job;
import com.studentos.model.User;
import com.studentos.util.InputValidator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/freelance/*")
public class FreelanceController extends HttpServlet {
    private JobDAO jobDAO = new JobDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if ("/services".equals(request.getPathInfo())) {
            request.getRequestDispatcher("/views/freelance/services.jsp").forward(request, response);
            return;
        }

        User user = (User) request.getSession().getAttribute("user");
        request.setAttribute("jobs", jobDAO.getAllJobs());
        request.setAttribute("currentUserId", user.getId());
        request.getRequestDispatcher("/views/freelance/index.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        String path = request.getPathInfo();
        
        if ("/post".equals(path)) {
            String title = InputValidator.trimToLength(request.getParameter("title"), 255);
            String description = InputValidator.trimToLength(request.getParameter("description"), 4000);
            Double budget = InputValidator.parseNonNegativeBudget(request.getParameter("budget"));
            if (user == null || title == null || description == null || budget == null) {
                response.sendRedirect(request.getContextPath() + "/freelance?error=invalid");
                return;
            }
            Job job = new Job();
            job.setUserId(user.getId());
            job.setTitle(title);
            job.setDescription(description);
            job.setBudget(budget);
            response.sendRedirect(request.getContextPath() + (jobDAO.createJob(job) ? "/freelance" : "/freelance?error=invalid"));
            return;
        }
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

}
