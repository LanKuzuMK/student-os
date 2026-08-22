package com.studentos.controller;

import com.studentos.dao.NotificationDAO;
import com.studentos.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/notifications/*")
public class NotificationController extends HttpServlet {
    private final NotificationDAO notificationDAO = new NotificationDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = signedInUser(request);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth/signin");
            return;
        }
        request.setAttribute("notifications", notificationDAO.getForRecipient(user.getId(), 60));
        request.getRequestDispatcher("/views/notifications/index.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = signedInUser(request);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth/signin");
            return;
        }
        if ("all".equals(request.getParameter("action"))) {
            notificationDAO.markAllRead(user.getId());
        } else {
            try {
                notificationDAO.markRead(Integer.parseInt(request.getParameter("notificationId")), user.getId());
            } catch (NumberFormatException ignored) {
                // Ignore malformed notification identifiers and keep the current page usable.
            }
        }
        response.sendRedirect(request.getContextPath() + "/notifications");
    }

    private User signedInUser(HttpServletRequest request) {
        return request.getSession(false) == null ? null : (User) request.getSession(false).getAttribute("user");
    }
}
