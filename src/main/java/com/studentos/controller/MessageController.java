package com.studentos.controller;

import com.studentos.dao.MessageDAO;
import com.studentos.model.Message;
import com.studentos.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/messages")
public class MessageController extends HttpServlet {
    private MessageDAO messageDAO = new MessageDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        req.setAttribute("messages", messageDAO.getMessagesForUser(user.getId()));
        req.getRequestDispatcher("/views/messages/index.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        Message msg = new Message();
        msg.setSenderId(user.getId());
        msg.setReceiverId(Integer.parseInt(req.getParameter("receiverId")));
        msg.setContent(req.getParameter("content"));
        messageDAO.sendMessage(msg);
        resp.sendRedirect(req.getContextPath() + "/messages");
    }
}
