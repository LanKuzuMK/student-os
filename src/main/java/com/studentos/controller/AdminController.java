package com.studentos.controller;

import com.studentos.dao.AdminDAO;
import com.studentos.dao.UserDAO;
import com.studentos.model.User;
import com.studentos.util.BCryptUtil;
import com.studentos.util.CsrfUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/admin/*")
public class AdminController extends HttpServlet {

    private final AdminDAO adminDAO = new AdminDAO();
    private final UserDAO userDAO = new UserDAO();

    // ── Guard helper ─────────────────────────────────────────────────────────

    private User requireAdmin(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession session = req.getSession(false);
        User sessionUser = session == null ? null : (User) session.getAttribute("user");
        User currentUser = sessionUser == null ? null : userDAO.findById(sessionUser.getId());
        if (currentUser == null || !"ADMIN".equals(currentUser.getRole()) || !"ACTIVE".equals(currentUser.getStatus())) {
            if (session != null) session.invalidate();
            res.sendError(403);
            return null;
        }
        session.setAttribute("user", currentUser);
        return currentUser;
    }

    // ── GET dispatcher ───────────────────────────────────────────────────────

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        User admin = requireAdmin(req, res);
        if (admin == null) return;
        CsrfUtil.getOrCreateToken(req);

        String path = req.getPathInfo();
        if (path == null) path = "/";

        switch (path) {
            case "/users":
                req.setAttribute("users", adminDAO.getAllUsers());
                forward(req, res, "/views/admin/users.jsp");
                break;
            case "/content":
                req.setAttribute("skills",   adminDAO.getAllSkills());
                req.setAttribute("jobs",     adminDAO.getAllJobs());
                req.setAttribute("services", adminDAO.getAllServices());
                forward(req, res, "/views/admin/content.jsp");
                break;
            case "/messages":
                req.setAttribute("messages", adminDAO.getRecentMessages(100));
                forward(req, res, "/views/admin/messages.jsp");
                break;
            default:
                // Dashboard
                req.setAttribute("stats", adminDAO.getPlatformStats());
                forward(req, res, "/views/admin/index.jsp");
        }
    }

    // ── POST dispatcher ──────────────────────────────────────────────────────

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        User admin = requireAdmin(req, res);
        if (admin == null) return;
        if (!CsrfUtil.hasValidToken(req)) {
            res.sendError(403);
            return;
        }

        String path = req.getPathInfo();
        if (path == null) path = "/";

        switch (path) {
            case "/users/ban":
                adminDAO.banUser(intParam(req, "userId"), admin.getId());
                res.sendRedirect(req.getContextPath() + "/admin/users?msg=banned");
                break;
            case "/users/unban":
                adminDAO.unbanUser(intParam(req, "userId"), admin.getId());
                res.sendRedirect(req.getContextPath() + "/admin/users?msg=unbanned");
                break;
            case "/users/delete":
                adminDAO.deleteUser(intParam(req, "userId"), admin.getId());
                res.sendRedirect(req.getContextPath() + "/admin/users?msg=deleted");
                break;
            case "/users/role":
                String requestedRole = req.getParameter("newRole");
                if ("ADMIN".equals(requestedRole) || "STUDENT".equals(requestedRole)) {
                    adminDAO.setRole(intParam(req, "userId"), requestedRole, admin.getId());
                    res.sendRedirect(req.getContextPath() + "/admin/users?msg=role_updated");
                } else {
                    res.sendRedirect(req.getContextPath() + "/admin/users?msg=invalid_role");
                }
                break;
            case "/users/reset-password": {
                String newPass = req.getParameter("newPassword");
                if (newPass != null && newPass.length() >= 6) {
                    String hash = BCryptUtil.hash(newPass);
                    adminDAO.resetPassword(intParam(req, "userId"), hash, admin.getId());
                }
                res.sendRedirect(req.getContextPath() + "/admin/users?msg=password_reset");
                break;
            }
            case "/content/delete-skill":
                adminDAO.deleteSkill(intParam(req, "id"));
                res.sendRedirect(req.getContextPath() + "/admin/content?msg=skill_deleted");
                break;
            case "/content/delete-job":
                adminDAO.deleteJob(intParam(req, "id"));
                res.sendRedirect(req.getContextPath() + "/admin/content?msg=job_deleted");
                break;
            case "/content/delete-service":
                adminDAO.deleteService(intParam(req, "id"));
                res.sendRedirect(req.getContextPath() + "/admin/content?msg=service_deleted");
                break;
            case "/messages/delete":
                adminDAO.deleteMessage(intParam(req, "id"));
                res.sendRedirect(req.getContextPath() + "/admin/messages?msg=deleted");
                break;
            default:
                res.sendRedirect(req.getContextPath() + "/admin");
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private void forward(HttpServletRequest req, HttpServletResponse res, String view)
            throws ServletException, IOException {
        req.getRequestDispatcher(view).forward(req, res);
    }

    private int intParam(HttpServletRequest req, String name) {
        try { return Integer.parseInt(req.getParameter(name)); }
        catch (NumberFormatException e) { return -1; }
    }
}
