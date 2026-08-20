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
import java.util.List;

@WebServlet("/goals")
public class GoalController extends HttpServlet {
    private final GoalDAO goalDAO = new GoalDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = getSessionUser(req);
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/signin");
            return;
        }
        List<Goal> goals = goalDAO.getGoalsByUserId(user.getId());
        req.setAttribute("goals", goals);
        req.setAttribute("goalCount", goals.size());
        req.getRequestDispatcher("/views/life/goals.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = getSessionUser(req);
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/signin");
            return;
        }

        String action = req.getParameter("action");
        if (action == null || action.isBlank()) {
            action = "create";
        }

        boolean changed;
        String notice;
        try {
            switch (action) {
                case "create" -> {
                    Goal goal = buildGoal(req, user.getId());
                    changed = goalDAO.createGoal(goal);
                    notice = changed ? "Goal added." : "We could not add that goal.";
                }
                case "update" -> {
                    Goal goal = buildGoal(req, user.getId());
                    goal.setId(parsePositiveId(req.getParameter("goalId")));
                    changed = goalDAO.updateGoal(goal);
                    notice = changed ? "Goal updated." : "We could not update that goal.";
                }
                case "progress" -> {
                    int goalId = parsePositiveId(req.getParameter("goalId"));
                    int progress = parseProgress(req.getParameter("progress"));
                    changed = goalDAO.updateProgress(goalId, user.getId(), progress);
                    notice = changed ? "Progress updated." : "We could not update progress.";
                }
                case "delete" -> {
                    int goalId = parsePositiveId(req.getParameter("goalId"));
                    changed = goalDAO.deleteGoal(goalId, user.getId());
                    notice = changed ? "Goal deleted." : "We could not delete that goal.";
                }
                default -> {
                    changed = false;
                    notice = "Unknown goal action.";
                }
            }
        } catch (IllegalArgumentException e) {
            changed = false;
            notice = e.getMessage();
        }

        String status = changed ? "success" : "error";
        resp.sendRedirect(req.getContextPath() + "/goals?" + status + "=" + java.net.URLEncoder.encode(notice, java.nio.charset.StandardCharsets.UTF_8));
    }

    private Goal buildGoal(HttpServletRequest req, int userId) {
        String title = safeText(req.getParameter("title"));
        if (title.isBlank()) {
            throw new IllegalArgumentException("Give your goal a title.");
        }
        Goal goal = new Goal();
        goal.setUserId(userId);
        goal.setTitle(title);
        goal.setDescription(safeText(req.getParameter("description")));
        goal.setProgress(parseProgress(req.getParameter("progress")));
        return goal;
    }

    private int parsePositiveId(String value) {
        try {
            int id = Integer.parseInt(value);
            if (id <= 0) {
                throw new NumberFormatException();
            }
            return id;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("That goal could not be found.");
        }
    }

    private int parseProgress(String value) {
        try {
            int progress = Integer.parseInt(value);
            if (progress < 0 || progress > 100) {
                throw new NumberFormatException();
            }
            return progress;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Progress must be between 0 and 100.");
        }
    }

    private String safeText(String value) {
        return value == null ? "" : value.trim();
    }

    private User getSessionUser(HttpServletRequest req) {
        return (User) req.getSession().getAttribute("user");
    }
}
