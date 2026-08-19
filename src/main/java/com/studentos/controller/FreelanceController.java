package com.studentos.controller;
import com.studentos.dao.JobDAO;
import com.studentos.model.Job;
import com.studentos.model.User;
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

        request.setAttribute("jobs", jobDAO.getAllJobs());
        request.getRequestDispatcher("/views/freelance/index.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        String path = request.getPathInfo();
        
        if ("/post".equals(path)) {
            Job job = new Job();
            job.setUserId(user.getId());
            job.setTitle(request.getParameter("title"));
            job.setDescription(request.getParameter("description"));
            job.setBudget(Double.parseDouble(request.getParameter("budget")));
            jobDAO.createJob(job);
            response.sendRedirect(request.getContextPath() + "/freelance");
        }
    }
}
