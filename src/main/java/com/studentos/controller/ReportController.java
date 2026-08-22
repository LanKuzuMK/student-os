package com.studentos.controller;

import com.studentos.dao.ModerationDAO;
import com.studentos.model.User;
import com.studentos.util.InputValidator;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Set;

/** Receives CSRF-protected reports for student-visible content. */
@WebServlet("/reports/*")
public class ReportController extends HttpServlet {
    private static final Set<String> REASONS = Set.of("SPAM", "HARASSMENT", "INAPPROPRIATE", "MISLEADING", "OTHER");
    private final ModerationDAO moderationDAO = new ModerationDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User reporter = (User) request.getSession(false).getAttribute("user");
        String returnTo = safeReturnTo(request.getParameter("returnTo"));
        String targetType = request.getParameter("targetType");
        String reason = request.getParameter("reason");
        String details = InputValidator.trimToLength(request.getParameter("details"), 1000);
        if (reporter == null || !REASONS.contains(reason) || !isTargetType(targetType)
                || !moderationDAO.createReport(reporter.getId(), targetType, intParam(request, "targetId"), reason, details)) {
            response.sendRedirect(request.getContextPath() + returnTo + "?report=0");
            return;
        }
        response.sendRedirect(request.getContextPath() + returnTo + "?report=1");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    private boolean isTargetType(String targetType) {
        return "SKILL".equals(targetType) || "JOB".equals(targetType) || "SERVICE".equals(targetType) || "MESSAGE".equals(targetType);
    }

    private int intParam(HttpServletRequest request, String name) {
        try { return Integer.parseInt(request.getParameter(name)); }
        catch (NumberFormatException exception) { return -1; }
    }

    private String safeReturnTo(String path) {
        return switch (path) {
            case "/skills/discover", "/freelance", "/messages" -> path;
            default -> "/dashboard";
        };
    }
}
