package com.studentos.controller;

import com.studentos.dao.MessageDAO;
import com.studentos.model.Message;
import com.studentos.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class MessageController extends HttpServlet {
    private final MessageDAO messageDAO = new MessageDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth/signin");
            return;
        }

        String recipientId = request.getParameter("to");
        if (recipientId != null) {
            try {
                int id = Integer.parseInt(recipientId);
                if (id > 0 && id != user.getId()) {
                    request.setAttribute("recipientId", id);
                }
            } catch (NumberFormatException ignored) {
                // An invalid recipient simply opens the normal inbox.
            }
        }

        request.setAttribute("messages", messageDAO.getMessagesForUser(user.getId()));
        request.getRequestDispatcher("/views/messages/index.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth/signin");
            return;
        }

        try {
            int receiverId = Integer.parseInt(request.getParameter("receiverId"));
            String content = request.getParameter("content");
            if (receiverId > 0 && receiverId != user.getId() && content != null && !content.isBlank()) {
                Message message = new Message();
                message.setSenderId(user.getId());
                message.setReceiverId(receiverId);
                message.setContent(content.trim());
                messageDAO.sendMessage(message);
                response.sendRedirect(request.getContextPath() + "/messages?sent=1");
                return;
            }
        } catch (NumberFormatException ignored) {
            // The view will show a simple compose error below.
        }

        response.sendRedirect(request.getContextPath() + "/messages?error=1");
    }
}
