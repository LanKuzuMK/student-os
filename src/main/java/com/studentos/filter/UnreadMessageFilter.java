package com.studentos.filter;

import com.studentos.dao.MessageDAO;
import com.studentos.dao.NotificationDAO;
import com.studentos.model.User;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class UnreadMessageFilter implements Filter {
    private final MessageDAO messageDAO = new MessageDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest httpRequest) {
            HttpSession session = httpRequest.getSession(false);
            if (session != null) {
                User user = (User) session.getAttribute("user");
                if (user != null) {
                    httpRequest.setAttribute("unreadMessageCount", messageDAO.countUnreadMessagesForUser(user.getId()));
                    httpRequest.setAttribute("unreadNotificationCount", notificationDAO.countUnreadForRecipient(user.getId()));
                }
            }
        }
        chain.doFilter(request, response);
    }
}
