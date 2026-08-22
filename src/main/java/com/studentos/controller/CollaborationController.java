package com.studentos.controller;

import com.studentos.dao.CollaborationRequestDAO;
import com.studentos.dao.NotificationDAO;
import com.studentos.dao.UserDAO;
import com.studentos.model.CollaborationRequest;
import com.studentos.model.User;
import com.studentos.util.CollaborationPolicy;
import com.studentos.util.InputValidator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/collaborations/*")
public class CollaborationController extends HttpServlet {
    private final CollaborationRequestDAO requestDAO = new CollaborationRequestDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = signedInUser(request, response);
        if (user == null) return;
        request.setAttribute("collaborationRequests", requestDAO.getForUser(user.getId()));
        User recipient = parseUser(request.getParameter("to"));
        if (recipient != null && recipient.getId() != user.getId() && "ACTIVE".equals(recipient.getStatus())) request.setAttribute("proposalRecipient", recipient);
        request.getRequestDispatcher("/views/collaborations/index.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = signedInUser(request, response);
        if (user == null) return;
        String path = request.getPathInfo();
        if ("/new".equals(path)) create(request, response, user);
        else if ("/respond".equals(path)) respond(request, response, user);
        else if ("/cancel".equals(path)) cancel(request, response, user);
        else response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    private void create(HttpServletRequest request, HttpServletResponse response, User sender) throws IOException {
        Integer recipientId = parseId(request.getParameter("recipientId"));
        String type = CollaborationPolicy.requestType(request.getParameter("requestType"));
        String title = InputValidator.trimToLength(request.getParameter("title"), 120);
        String description = InputValidator.trimToLength(request.getParameter("description"), 1000);
        String commitment = InputValidator.trimToLength(request.getParameter("expectedCommitment"), 120);
        if (recipientId == null || type == null || title == null || title.length() < 6 || description == null || description.length() < 20) {
            response.sendRedirect(request.getContextPath() + "/collaborations?error=proposal"); return;
        }
        CollaborationRequest proposal = new CollaborationRequest();
        proposal.setRequesterId(sender.getId()); proposal.setRecipientId(recipientId); proposal.setRequestType(type); proposal.setTitle(title); proposal.setDescription(description); proposal.setExpectedCommitment(commitment);
        if (requestDAO.create(proposal)) {
            notificationDAO.create(recipientId, "COLLABORATION", "New collaboration proposal", "A student sent you a structured collaboration proposal.", "/collaborations");
            response.sendRedirect(request.getContextPath() + "/collaborations?sent=1"); return;
        }
        response.sendRedirect(request.getContextPath() + "/collaborations?error=duplicate");
    }

    private void respond(HttpServletRequest request, HttpServletResponse response, User recipient) throws IOException {
        Integer requestId = parseId(request.getParameter("requestId"));
        String status = CollaborationPolicy.responseStatus(request.getParameter("status"));
        String note = InputValidator.trimToLength(request.getParameter("responseNote"), 500);
        Integer requesterId = requestId == null ? null : requestDAO.getRequesterId(requestId);
        if (requestId != null && status != null && requestDAO.respond(requestId, recipient.getId(), status, note)) {
            if (requesterId != null) notificationDAO.create(requesterId, "COLLABORATION", "Collaboration proposal updated", "Your collaboration proposal was " + status.toLowerCase() + ".", "/collaborations");
            response.sendRedirect(request.getContextPath() + "/collaborations?responded=1"); return;
        }
        response.sendRedirect(request.getContextPath() + "/collaborations?error=response");
    }

    private void cancel(HttpServletRequest request, HttpServletResponse response, User requester) throws IOException {
        Integer requestId = parseId(request.getParameter("requestId"));
        response.sendRedirect(request.getContextPath() + "/collaborations?cancelled=" + (requestId != null && requestDAO.cancel(requestId, requester.getId()) ? "1" : "0"));
    }

    private User signedInUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = request.getSession(false) == null ? null : (User) request.getSession(false).getAttribute("user");
        if (user == null) response.sendRedirect(request.getContextPath() + "/auth/signin");
        return user;
    }

    private User parseUser(String value) { Integer id = parseId(value); return id == null ? null : userDAO.findById(id); }
    private Integer parseId(String value) { try { int id = Integer.parseInt(value); return id > 0 ? id : null; } catch (NumberFormatException exception) { return null; } }
}
