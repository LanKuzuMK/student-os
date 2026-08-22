package com.studentos.controller;

import com.studentos.dao.AdminDAO;
import com.studentos.dao.ModerationDAO;
import com.studentos.dao.NotificationDAO;
import com.studentos.dao.UserDAO;
import com.studentos.model.User;
import com.studentos.util.BCryptUtil;
import com.studentos.util.CsrfUtil;
import com.studentos.util.InputValidator;
import com.studentos.util.AccessPolicy;
import com.studentos.util.ModerationPolicy;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/admin/*")
public class AdminController extends HttpServlet {

    private final AdminDAO adminDAO = new AdminDAO();
    private final ModerationDAO moderationDAO = new ModerationDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();
    private final UserDAO userDAO = new UserDAO();

    // ── Guard helper ─────────────────────────────────────────────────────────

    private User requireStaff(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession session = req.getSession(false);
        User sessionUser = session == null ? null : (User) session.getAttribute("user");
        User currentUser = sessionUser == null ? null : userDAO.findById(sessionUser.getId());
        if (currentUser == null || !isStaff(currentUser) || !"ACTIVE".equals(currentUser.getStatus())) {
            if (session != null) session.invalidate();
            res.sendError(403);
            return null;
        }
        session.setAttribute("user", currentUser);
        session.setAttribute("authVersion", currentUser.getAuthVersion());
        return currentUser;
    }

    private boolean requireAdmin(User staff, HttpServletResponse res) throws IOException {
        if (AccessPolicy.isAdminRole(staff.getRole())) return true;
        res.sendError(403);
        return false;
    }

    private boolean isStaff(User user) {
        return AccessPolicy.isStaffRole(user.getRole());
    }

    // ── GET dispatcher ───────────────────────────────────────────────────────

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        User staff = requireStaff(req, res);
        if (staff == null) return;
        CsrfUtil.getOrCreateToken(req);

        String path = req.getPathInfo();
        if (path == null) path = "/";

        switch (path) {
            case "/users":
                if (!requireAdmin(staff, res)) return;
                String userQuery = InputValidator.trimToLength(req.getParameter("q"), 100);
                String userRole = allowedUserRole(req.getParameter("role"));
                String userStatus = allowedUserStatus(req.getParameter("status"));
                int userPage = Math.max(1, intParam(req, "page"));
                int userPageSize = 20;
                int totalUsers = adminDAO.countUsers(userQuery, userRole, userStatus);
                req.setAttribute("users", adminDAO.getUsers(userQuery, userRole, userStatus, userPageSize, (userPage - 1) * userPageSize));
                req.setAttribute("userQuery", userQuery);
                req.setAttribute("userRole", userRole);
                req.setAttribute("userStatus", userStatus);
                req.setAttribute("userTotal", totalUsers);
                req.setAttribute("userPage", userPage);
                req.setAttribute("userPages", Math.max(1, (int) Math.ceil(totalUsers / (double) userPageSize)));
                forward(req, res, "/views/admin/users.jsp");
                break;
            case "/content":
                req.setAttribute("skills",   adminDAO.getAllSkills());
                req.setAttribute("jobs",     adminDAO.getAllJobs());
                req.setAttribute("services", adminDAO.getAllServices());
                forward(req, res, "/views/admin/content.jsp");
                break;
            case "/messages":
                if (!requireAdmin(staff, res)) return;
                req.setAttribute("messages", adminDAO.getRecentMessages(100));
                forward(req, res, "/views/admin/messages.jsp");
                break;
            case "/reports":
                String status = allowedReportStatus(req.getParameter("status"));
                String targetType = allowedTargetType(req.getParameter("type"));
                String query = InputValidator.trimToLength(req.getParameter("q"), 100);
                int page = Math.max(1, intParam(req, "page"));
                int pageSize = 20;
                int totalReports = moderationDAO.countReports(status, targetType, query);
                req.setAttribute("reports", moderationDAO.getReports(status, targetType, query, pageSize, (page - 1) * pageSize));
                req.setAttribute("reportStatus", status);
                req.setAttribute("reportType", targetType);
                req.setAttribute("reportQuery", query);
                req.setAttribute("reportPage", page);
                req.setAttribute("reportPages", Math.max(1, (int) Math.ceil(totalReports / (double) pageSize)));
                forward(req, res, "/views/admin/reports.jsp");
                break;
            case "/audit":
                req.setAttribute("auditEntries", moderationDAO.getAuditEntries(250));
                forward(req, res, "/views/admin/audit.jsp");
                break;
            default:
                if (!requireAdmin(staff, res)) return;
                req.setAttribute("stats", adminDAO.getPlatformStats());
                forward(req, res, "/views/admin/index.jsp");
        }
    }

    // ── POST dispatcher ──────────────────────────────────────────────────────

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        User staff = requireStaff(req, res);
        if (staff == null) return;
        if (!CsrfUtil.hasValidToken(req)) {
            res.sendError(403);
            return;
        }

        String path = req.getPathInfo();
        if (path == null) path = "/";

        switch (path) {
            case "/users/ban":
                if (!requireAdmin(staff, res)) return;
                int bannedUserId = intParam(req, "userId");
                if (adminDAO.banUser(bannedUserId, staff.getId())) {
                    audit(staff, "USER_BANNED", "USER", bannedUserId, null);
                    notifyUser(bannedUserId, "ACCOUNT", "Account access updated", "Your StudentOS account has been restricted. Contact an administrator if you need help.", "/dashboard");
                }
                res.sendRedirect(req.getContextPath() + "/admin/users?msg=banned");
                break;
            case "/users/unban":
                if (!requireAdmin(staff, res)) return;
                int unbannedUserId = intParam(req, "userId");
                if (adminDAO.unbanUser(unbannedUserId, staff.getId())) {
                    audit(staff, "USER_UNBANNED", "USER", unbannedUserId, null);
                    notifyUser(unbannedUserId, "ACCOUNT", "Account access restored", "Your StudentOS account is active again.", "/dashboard");
                }
                res.sendRedirect(req.getContextPath() + "/admin/users?msg=unbanned");
                break;
            case "/users/delete":
                if (!requireAdmin(staff, res)) return;
                adminDAO.deleteUser(intParam(req, "userId"), staff.getId());
                audit(staff, "USER_DELETED", "USER", intParam(req, "userId"), null);
                res.sendRedirect(req.getContextPath() + "/admin/users?msg=deleted");
                break;
            case "/users/role":
                if (!requireAdmin(staff, res)) return;
                String requestedRole = req.getParameter("newRole");
                int roleUserId = intParam(req, "userId");
                if ("ADMIN".equals(requestedRole) || "MODERATOR".equals(requestedRole) || "STUDENT".equals(requestedRole)) {
                    if (adminDAO.setRole(roleUserId, requestedRole, staff.getId())) {
                        audit(staff, "ROLE_CHANGED", "USER", roleUserId, requestedRole);
                        notifyUser(roleUserId, "ACCOUNT", "StudentOS role updated", "Your StudentOS role is now " + requestedRole + ".", "/dashboard");
                    }
                    res.sendRedirect(req.getContextPath() + "/admin/users?msg=role_updated");
                } else {
                    res.sendRedirect(req.getContextPath() + "/admin/users?msg=invalid_role");
                }
                break;
            case "/users/reset-password": {
                if (!requireAdmin(staff, res)) return;
                String newPass = req.getParameter("newPassword");
                if (InputValidator.isValidPassword(newPass)) {
                    String hash = BCryptUtil.hash(newPass);
                    int resetUserId = intParam(req, "userId");
                    if (adminDAO.resetPassword(resetUserId, hash, staff.getId())) {
                        audit(staff, "PASSWORD_RESET", "USER", resetUserId, null);
                        notifyUser(resetUserId, "ACCOUNT", "Password reset by an administrator", "An administrator reset your StudentOS password. Use the new password provided by the administrator, then change it from My Profile.", "/account/password");
                    }
                }
                res.sendRedirect(req.getContextPath() + "/admin/users?msg=password_reset");
                break;
            }
            case "/content/delete-skill":
                if (!requireAdmin(staff, res)) return;
                adminDAO.deleteSkill(intParam(req, "id"));
                audit(staff, "CONTENT_DELETED", "SKILL", intParam(req, "id"), null);
                res.sendRedirect(req.getContextPath() + "/admin/content?msg=skill_deleted");
                break;
            case "/content/delete-job":
                if (!requireAdmin(staff, res)) return;
                adminDAO.deleteJob(intParam(req, "id"));
                audit(staff, "CONTENT_DELETED", "JOB", intParam(req, "id"), null);
                res.sendRedirect(req.getContextPath() + "/admin/content?msg=job_deleted");
                break;
            case "/content/delete-service":
                if (!requireAdmin(staff, res)) return;
                adminDAO.deleteService(intParam(req, "id"));
                audit(staff, "CONTENT_DELETED", "SERVICE", intParam(req, "id"), null);
                res.sendRedirect(req.getContextPath() + "/admin/content?msg=service_deleted");
                break;
            case "/messages/delete":
                if (!requireAdmin(staff, res)) return;
                adminDAO.deleteMessage(intParam(req, "id"));
                audit(staff, "MESSAGE_DELETED", "MESSAGE", intParam(req, "id"), null);
                res.sendRedirect(req.getContextPath() + "/admin/messages?msg=deleted");
                break;
            case "/content/hide":
                setVisibility(req, res, staff, true, "/admin/content");
                break;
            case "/content/restore":
                setVisibility(req, res, staff, false, "/admin/content");
                break;
            case "/reports/assign": {
                int reportId = intParam(req, "reportId");
                boolean assigned = moderationDAO.assignReport(reportId, staff.getId());
                if (assigned) audit(staff, "REPORT_ASSIGNED", "REPORT", reportId, "Assigned to reviewer");
                res.sendRedirect(req.getContextPath() + "/admin/reports?msg=" + (assigned ? "assigned" : "assign_failed"));
                break;
            }
            case "/reports/hide": {
                int reportId = intParam(req, "reportId");
                String type = req.getParameter("targetType");
                int targetId = intParam(req, "targetId");
                String reason = InputValidator.trimToLength(req.getParameter("reason"), 1000);
                Integer reporterId = moderationDAO.getReporterId(reportId);
                boolean hidden = moderationDAO.setContentVisibility(type, targetId, true, reason);
                boolean resolved = moderationDAO.resolveReport(reportId, staff.getId(), "RESOLVED", reason);
                if (hidden) audit(staff, "CONTENT_HIDDEN", type, targetId, reason);
                if (resolved) {
                    audit(staff, "REPORT_RESOLVED", "REPORT", reportId, reason);
                    notifyReportOutcome(reporterId, "Your report was resolved", "StudentOS reviewed your report and took the appropriate content action.");
                }
                res.sendRedirect(req.getContextPath() + "/admin/reports?msg=" + (hidden && resolved ? "hidden_and_resolved" : "review_failed"));
                break;
            }
            case "/reports/dismiss": {
                int reportId = intParam(req, "reportId");
                String reason = InputValidator.trimToLength(req.getParameter("reason"), 1000);
                Integer reporterId = moderationDAO.getReporterId(reportId);
                boolean dismissed = moderationDAO.resolveReport(reportId, staff.getId(), "DISMISSED", reason);
                if (dismissed) {
                    audit(staff, "REPORT_DISMISSED", "REPORT", reportId, reason);
                    notifyReportOutcome(reporterId, "Your report was reviewed", "StudentOS reviewed your report and closed the case.");
                }
                res.sendRedirect(req.getContextPath() + "/admin/reports?msg=" + (dismissed ? "dismissed" : "review_failed"));
                break;
            }
            case "/reports/resolve": {
                int reportId = intParam(req, "reportId");
                String reason = InputValidator.trimToLength(req.getParameter("reason"), 1000);
                Integer reporterId = moderationDAO.getReporterId(reportId);
                boolean resolved = moderationDAO.resolveReport(reportId, staff.getId(), "RESOLVED", reason);
                if (resolved) {
                    audit(staff, "REPORT_RESOLVED", "REPORT", reportId, reason);
                    notifyReportOutcome(reporterId, "Your report was resolved", "StudentOS reviewed your report and closed the case.");
                }
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
        boolean changed = moderationDAO.setContentVisibility(type, targetId, hidden, reason);
        if (changed) audit(admin, hidden ? "CONTENT_HIDDEN" : "CONTENT_RESTORED", type, targetId, reason);
        res.sendRedirect(req.getContextPath() + redirect + "?msg=" + (changed ? (hidden ? "content_hidden" : "content_restored") : "content_update_failed"));
    }

    private void audit(User admin, String action, String targetType, int targetId, String reason) {
        if (targetId > 0) moderationDAO.recordAudit(admin.getId(), action, targetType, targetId, reason);
    }

    private void notifyUser(int userId, String type, String title, String message, String actionUrl) {
        if (userId > 0) notificationDAO.create(userId, type, title, message, actionUrl);
    }

    private void notifyReportOutcome(Integer reporterId, String title, String message) {
        if (reporterId != null) notificationDAO.create(reporterId, "MODERATION", title, message, "/notifications");
    }

    private String allowedReportStatus(String status) {
        return ModerationPolicy.isKnownReportStatus(status) ? status : null;
    }

    private String allowedTargetType(String targetType) {
        return ModerationPolicy.isKnownTargetType(targetType) ? targetType : null;
    }

    private String allowedUserRole(String role) {
        return "ADMIN".equals(role) || "MODERATOR".equals(role) || "STUDENT".equals(role) ? role : null;
    }

    private String allowedUserStatus(String status) {
        return "ACTIVE".equals(status) || "BANNED".equals(status) ? status : null;
    }
}
