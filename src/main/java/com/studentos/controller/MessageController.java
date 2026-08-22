package com.studentos.controller;

import com.studentos.dao.MessageDAO;
import com.studentos.dao.NotificationDAO;
import com.studentos.dao.UserDAO;
import com.studentos.dao.UserBlockDAO;
import com.studentos.model.Message;
import com.studentos.model.User;
import com.studentos.util.CsrfUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class MessageController extends HttpServlet {
    private final MessageDAO messageDAO = new MessageDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();
    private final UserDAO userDAO = new UserDAO();
    private final UserBlockDAO userBlockDAO = new UserBlockDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = getSignedInUser(request, response);
        if (user == null) {
            return;
        }

        String recipientEmail = normalizeEmail(request.getParameter("toEmail"));
        if (recipientEmail == null) {
            String recipientId = request.getParameter("to");
            if (recipientId != null) {
                try {
                    int id = Integer.parseInt(recipientId);
                    User recipient = userDAO.findById(id);
                    if (recipient != null && recipient.getId() != user.getId()) {
                        recipientEmail = recipient.getEmail();
                    }
                } catch (NumberFormatException ignored) {
                    // An invalid Discover link simply opens the normal inbox.
                }
            }
        }

        if (recipientEmail != null && !recipientEmail.equalsIgnoreCase(user.getEmail())) {
            request.setAttribute("recipientEmail", recipientEmail);
        }
        List<Message> messages = messageDAO.getMessagesForUser(user.getId());
        Set<Integer> blockedIds = userBlockDAO.getBlockedUserIds(user.getId());
        for (Message message : messages) message.setCounterpartBlocked(blockedIds.contains(message.getCounterpartId()));
        request.setAttribute("messages", messages);
        request.setAttribute("currentUserId", user.getId());
        messageDAO.markMessagesReadForUser(user.getId());
        request.setAttribute("unreadMessageCount", 0);
        request.getRequestDispatcher("/views/messages/index.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = getSignedInUser(request, response);
        if (user == null) {
            return;
        }

        if (!CsrfUtil.hasValidToken(request)) {
            response.sendRedirect(request.getContextPath() + "/messages?error=csrf");
            return;
        }

        if ("clear".equals(request.getParameter("action"))) {
            clearConversation(request, response, user);
        } else if ("block".equals(request.getParameter("action"))) {
            updateBlock(request, response, user, true);
        } else if ("unblock".equals(request.getParameter("action"))) {
            updateBlock(request, response, user, false);
        } else {
            sendMessage(request, response, user);
        }
    }

    private void sendMessage(HttpServletRequest request, HttpServletResponse response, User sender) throws IOException {
        String recipientEmail = normalizeEmail(request.getParameter("recipientEmail"));
        String content = request.getParameter("content");
        User recipient = recipientEmail == null ? null : userDAO.findByEmail(recipientEmail);

        if (recipient != null && recipient.getId() != sender.getId() && content != null && !content.isBlank()
                && !userBlockDAO.isContactBlocked(sender.getId(), recipient.getId())) {
            Message message = new Message();
            message.setSenderId(sender.getId());
            message.setReceiverId(recipient.getId());
            message.setContent(content.trim());
            if (messageDAO.sendMessage(message)) {
                notificationDAO.create(recipient.getId(), "MESSAGE", "New StudentOS message", "You have a new message from a student.", "/messages");
                response.sendRedirect(request.getContextPath() + "/messages?sent=1");
                return;
            }
        }
        response.sendRedirect(request.getContextPath() + "/messages?error=" + (recipient != null ? "blocked" : "1"));
    }

    private void clearConversation(HttpServletRequest request, HttpServletResponse response, User user) throws IOException {
        String counterpartEmail = normalizeEmail(request.getParameter("counterpartEmail"));
        User counterpart = counterpartEmail == null ? null : userDAO.findByEmail(counterpartEmail);
        if (counterpart != null && counterpart.getId() != user.getId()
                && messageDAO.clearConversationForUser(user.getId(), counterpart.getId())) {
            response.sendRedirect(request.getContextPath() + "/messages?cleared=1");
            return;
        }
        response.sendRedirect(request.getContextPath() + "/messages?clearError=1");
    }

    private void updateBlock(HttpServletRequest request, HttpServletResponse response, User user, boolean block) throws IOException {
        String counterpartEmail = normalizeEmail(request.getParameter("counterpartEmail"));
        User counterpart = counterpartEmail == null ? null : userDAO.findByEmail(counterpartEmail);
        boolean changed = counterpart != null && counterpart.getId() != user.getId()
                && (block ? userBlockDAO.block(user.getId(), counterpart.getId()) : userBlockDAO.unblock(user.getId(), counterpart.getId()));
        response.sendRedirect(request.getContextPath() + "/messages?" + (changed ? (block ? "blocked=1" : "unblocked=1") : "blockError=1"));
    }

    private User getSignedInUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth/signin");
            return null;
        }
        return user;
    }

    private String normalizeEmail(String value) {
        if (value == null) {
            return null;
        }
        String email = value.trim().toLowerCase();
        return email.isEmpty() ? null : email;
    }
}
