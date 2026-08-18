package com.studentos.controller;

import com.studentos.dao.GoalDAO;
import com.studentos.model.Goal;
import com.studentos.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/goals")
public class GoalController extends HttpServlet {
    private GoalDAO goalDAO = new GoalDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        req.setAttribute("goals", goalDAO.getGoalsByUserId(user.getId()));
        req.getRequestDispatcher("/views/life/goals.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        Goal goal = new Goal();
        goal.setUserId(user.getId());
        goal.setTitle(req.getParameter("title"));
        goal.setDescription(req.getParameter("description"));
        goal.setProgress(Integer.parseInt(req.getParameter("progress")));
        goalDAO.createGoal(goal);
        resp.sendRedirect(req.getContextPath() + "/goals");
    }
}
