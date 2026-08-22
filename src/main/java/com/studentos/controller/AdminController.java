package com.studentos.controller;

import com.studentos.dao.AdminDAO;
import com.studentos.dao.ModerationDAO;
import com.studentos.dao.UserDAO;
import com.studentos.model.User;
import com.studentos.util.BCryptUtil;
import com.studentos.util.CsrfUtil;
import com.studentos.util.InputValidator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/admin/*")
public class AdminController extends HttpServlet {

    private final AdminDAO adminDAO = new AdminDAO();
    private final ModerationDAO moderationDAO = new ModerationDAO();
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
            case "/reports":
                req.setAttribute("reports", moderationDAO.getReports());
                forward(req, res, "/views/admin/reports.jsp");
                break;
            case "/audit":
                req.setAttribute("auditEntries", moderationDAO.getAuditEntries(250));
                forward(req, res, "/views/admin/audit.jsp");
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
                audit(admin, "USER_BANNED", "USER", intParam(req, "userId"), null);
                res.sendRedirect(req.getContextPath() + "/admin/users?msg=banned");
                break;
            case "/users/unban":
                adminDAO.unbanUser(intParam(req, "userId"), admin.getId());
                audit(admin, "USER_UNBANNED", "USER", intParam(req, "userId"), null);
                res.sendRedirect(req.getContextPath() + "/admin/users?msg=unbanned");
                break;
            case "/users/delete":
                adminDAO.deleteUser(intParam(req, "userId"), admin.getId());
                audit(admin, "USER_DELETED", "USER", intParam(req, "userId"), null);
                res.sendRedirect(req.getContextPath() + "/admin/users?msg=deleted");
                break;
            case "/users/role":
                String requestedRole = req.getParameter("newRole");
                if ("ADMIN".equals(requestedRole) || "STUDENT".equals(requestedRole)) {
                    adminDAO.setRole(intParam(req, "userId"), requestedRole, admin.getId());
                    audit(admin, "ROLE_CHANGED", "USER", intParam(req, "userId"), requestedRole);
                    res.sendRedirect(req.getContextPath() + "/admin/users?msg=role_updated");
                } else {
                    res.sendRedirect(req.getContextPath() + "/admin/users?msg=invalid_role");
                }
                break;
            case "/users/reset-password": {
                String newPass = req.getParameter("newPassword");
                if (InputValidator.isValidPassword(newPass)) {
                    String hash = BCryptUtil.hash(newPass);
                    adminDAO.resetPassword(intParam(req, "userId"), hash, admin.getId());
                    audit(admin, "PASSWORD_RESET", "USER", intParam(req, "userId"), null);
                }
                res.sendRedirect(req.getContextPath() + "/admin/users?msg=password_reset");
                break;
            }
            case "/content/delete-skill":
                adminDAO.deleteSkill(intParam(req, "id"));
                audit(admin, "CONTENT_DELETED", "SKILL", intParam(req, "id"), null);
                res.sendRedirect(req.getContextPath() + "/admin/content?msg=skill_deleted");
                break;
            case "/content/delete-job":
                adminDAO.deleteJob(intParam(req, "id"));
                audit(admin, "CONTENT_DELETED", "JOB", intParam(req, "id"), null);
                res.sendRedirect(req.getContextPath() + "/admin/content?msg=job_deleted");
                break;
            case "/content/delete-service":
                adminDAO.deleteService(intParam(req, "id"));
                audit(admin, "CONTENT_DELETED", "SERVICE", intParam(req, "id"), null);
                res.sendRedirect(req.getContextPath() + "/admin/content?msg=service_deleted");
                break;
            case "/messages/delete":
                adminDAO.deleteMessage(intParam(req, "id"));
                audit(admin, "MESSAGE_DELETED", "MESSAGE", intParam(req, "id"), null);
                res.sendRedirect(req.getContextPath() + "/admin/messages?msg=deleted");
                break;
            case "/content/hide":
                setVisibility(req, res, admin, true, "/admin/content");
                break;
            case "/content/restore":
                setVisibility(req, res, admin, false, "/admin/content");
                break;
            case "/reports/hide": {
                String type = req.getParameter("targetType");
                int targetId = intParam(req, "targetId");
                String reason = InputValidator.trimToLength(req.getParameter("reason"), 1000);
                boolean hidden = moderationDAO.setContentVisibility(type, targetId, true);
                boolean resolved = moderationDAO.resolveReport(intParam(req, "reportId"), admin.getId(), "RESOLVED", reason);
                if (hidden) audit(admin, "CONTENT_HIDDEN", type, targetId, reason);
                if (resolved) audit(admin, "REPORT_RESOLVED", "REPORT", intParam(req, "reportId"), reason);
                res.sendRedirect(req.getContextPath() + "/admin/reports?msg=" + (hidden && resolved ? "hidden_and_resolved" : "review_failed"));
                break;
            }
            case "/reports/dismiss": {
                String reason = InputValidator.trimToLength(req.getParameter("reason"), 1000);
                boolean dismissed = moderationDAO.resolveReport(intParam(req, "reportId"), admin.getId(), "DISMISSED", reason);
                if (dismissed) audit(admin, "REPORT_DISMISSED", "REPORT", intParam(req, "reportId"), reason);
                res.sendRedirect(req.getContextPath() + "/admin/reports?msg=" + (dismissed ? "dismissed" : "review_failed"));
                break;
            }
            case "/reports/resolve": {
                String reason = InputValidator.trimToLength(req.getParameter("reason"), 1000);
                boolean resolved = moderationDAO.resolveReport(intParam(req, "reportId"), admin.getId(), "RESOLVED", reason);
                if (resolved) audit(admin, "REPORT_RESOLVED", "REPORT", intParam(req, "reportId"), reason);
                res.sendRedirect(req.getContextPath() + "/admin/reports?msg=" + (resolved ? "resolved" : "review_failed"));
                break;
            }
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

    private void setVisibility(HttpServletRequest req, HttpServletResponse res, User admin, boolean hidden, String redirect) throws IOException {
        String type = req.getParameter("targetType");
        int targetId = intParam(req, "targetId");
        String reason = InputValidator.trimToLength(req.getParameter("reason"), 1000);
        boolean changed = moderationDAO.setContentVisibility(type, targetId, hidden);
        if (changed) audit(admin, hidden ? "CONTENT_HIDDEN" : "CONTENT_RESTORED", type, targetId, reason);
        res.sendRedirect(req.getContextPath() + redirect + "?msg=" + (changed ? (hidden ? "content_hidden" : "content_restored") : "content_update_failed"));
    }

    private void audit(User admin, String action, String targetType, int targetId, String reason) {
        if (targetId > 0) moderationDAO.recordAudit(admin.getId(), action, targetType, targetId, reason);
    }
}
