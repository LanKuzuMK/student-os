package com.studentos.controller;

import com.studentos.dao.SavedItemDAO;
import com.studentos.model.User;
import com.studentos.util.SavedItemPolicy;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/saved/*")
public class SavedItemController extends HttpServlet {
    private final SavedItemDAO savedItemDAO = new SavedItemDAO();

    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = signedIn(request, response); if (user == null) return;
        String type = SavedItemPolicy.targetType(request.getParameter("type"));
        request.setAttribute("savedItems", savedItemDAO.getForOwner(user.getId(), type)); request.setAttribute("savedType", type);
        request.getRequestDispatcher("/views/saved/index.jsp").forward(request, response);
    }

    @Override protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = signedIn(request, response); if (user == null) return;
        String path = request.getPathInfo();
        if ("/add".equals(path)) add(request, response, user); else if ("/remove".equals(path)) remove(request, response, user); else response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    private void add(HttpServletRequest request, HttpServletResponse response, User user) throws IOException {
        String type = SavedItemPolicy.targetType(request.getParameter("targetType")); Integer targetId = parseId(request.getParameter("targetId"));
        String returnTo = safeReturn(request.getParameter("returnTo"));
        boolean saved = type != null && targetId != null && savedItemDAO.save(user.getId(), type, targetId);
        response.sendRedirect(request.getContextPath() + returnTo + (returnTo.contains("?") ? "&" : "?") + (saved ? "saved=1" : "saveError=1"));
    }

    private void remove(HttpServletRequest request, HttpServletResponse response, User user) throws IOException {
        Integer id = parseId(request.getParameter("id"));
        response.sendRedirect(request.getContextPath() + "/saved?removed=" + (id != null && savedItemDAO.remove(id, user.getId()) ? "1" : "0"));
    }

    private User signedIn(HttpServletRequest request, HttpServletResponse response) throws IOException { User user = request.getSession(false) == null ? null : (User) request.getSession(false).getAttribute("user"); if (user == null) response.sendRedirect(request.getContextPath() + "/auth/signin"); return user; }
    private Integer parseId(String value) { try { int id = Integer.parseInt(value); return id > 0 ? id : null; } catch (NumberFormatException exception) { return null; } }
    private String safeReturn(String value) { return value != null && (value.startsWith("/skills") || value.startsWith("/freelance") || value.startsWith("/profile")) ? value : "/saved"; }
}
