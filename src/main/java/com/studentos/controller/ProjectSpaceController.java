package com.studentos.controller;

import com.studentos.dao.NotificationDAO;
import com.studentos.dao.ProjectSpaceDAO;
import com.studentos.model.ProjectMember;
import com.studentos.model.ProjectSpace;
import com.studentos.model.User;
import com.studentos.util.InputValidator;
import com.studentos.util.ProjectSpacePolicy;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;

@WebServlet("/projects/*")
public class ProjectSpaceController extends HttpServlet {
    private final ProjectSpaceDAO projectDAO = new ProjectSpaceDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();

    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = signedIn(request, response); if (user == null) return;
        Integer projectId = parseId(request.getParameter("id"));
        if (projectId == null) { request.setAttribute("projects", projectDAO.getForMember(user.getId())); request.getRequestDispatcher("/views/projects/index.jsp").forward(request, response); return; }
        ProjectSpace project = projectDAO.getForMember(projectId, user.getId());
        if (project == null) { response.sendError(HttpServletResponse.SC_FORBIDDEN); return; }
        request.setAttribute("project", project); request.setAttribute("projectMembers", projectDAO.getMembers(projectId, user.getId())); request.setAttribute("eligibleCollaborators", projectDAO.getAcceptedCollaborators(user.getId())); request.setAttribute("projectMilestones", projectDAO.getMilestones(projectId, user.getId())); request.setAttribute("projectTasks", projectDAO.getTasks(projectId, user.getId()));
        request.getRequestDispatcher("/views/projects/workspace.jsp").forward(request, response);
    }

    @Override protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = signedIn(request, response); if (user == null) return;
        String path = request.getPathInfo();
        if ("/new".equals(path)) create(request, response, user); else if ("/members/add".equals(path)) addMember(request, response, user); else if ("/milestones/new".equals(path)) addMilestone(request, response, user); else if ("/tasks/new".equals(path)) addTask(request, response, user); else if ("/tasks/status".equals(path)) updateTaskStatus(request, response, user); else response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    private void create(HttpServletRequest request, HttpServletResponse response, User user) throws IOException {
        String title = InputValidator.trimToLength(request.getParameter("title"), 120); String description = InputValidator.trimToLength(request.getParameter("description"), 1000);
        Integer id = title == null || title.length() < 4 ? null : projectDAO.createProject(user.getId(), title, description);
        response.sendRedirect(request.getContextPath() + (id == null ? "/projects?error=create" : "/projects?id=" + id));
    }

    private void addMember(HttpServletRequest request, HttpServletResponse response, User user) throws IOException {
        Integer projectId = parseId(request.getParameter("projectId")); Integer memberId = parseId(request.getParameter("memberId")); String role = ProjectSpacePolicy.memberRole(request.getParameter("role"));
        boolean added = projectId != null && memberId != null && role != null && projectDAO.addAcceptedCollaborator(projectId, user.getId(), memberId, role);
        if (added) notificationDAO.create(memberId, "PROJECT", "Added to a Project Space", "A student added you to a private project workspace after your accepted collaboration.", "/projects?id=" + projectId);
        response.sendRedirect(request.getContextPath() + "/projects?id=" + (projectId == null ? "" : projectId) + (added ? "&memberAdded=1" : "&error=member"));
    }

    private void addMilestone(HttpServletRequest request, HttpServletResponse response, User user) throws IOException {
        Integer projectId = parseId(request.getParameter("projectId")); String title = InputValidator.trimToLength(request.getParameter("title"), 120); String description = InputValidator.trimToLength(request.getParameter("description"), 500); Date dueDate = parseDate(request.getParameter("dueDate"));
        boolean created = projectId != null && title != null && title.length() >= 4 && projectDAO.addMilestone(projectId, user.getId(), title, description, dueDate);
        response.sendRedirect(request.getContextPath() + "/projects?id=" + (projectId == null ? "" : projectId) + (created ? "&milestoneAdded=1" : "&error=milestone"));
    }

    private void addTask(HttpServletRequest request, HttpServletResponse response, User user) throws IOException {
        Integer projectId = parseId(request.getParameter("projectId")); Integer assignee = parseId(request.getParameter("assigneeId")); String title = InputValidator.trimToLength(request.getParameter("title"), 120); String description = InputValidator.trimToLength(request.getParameter("description"), 1000); String priority = ProjectSpacePolicy.priority(request.getParameter("priority"));
        boolean created = projectId != null && title != null && title.length() >= 3 && priority != null && projectDAO.addTask(projectId, user.getId(), title, description, priority, assignee);
        response.sendRedirect(request.getContextPath() + "/projects?id=" + (projectId == null ? "" : projectId) + (created ? "&taskAdded=1" : "&error=task"));
    }

    private void updateTaskStatus(HttpServletRequest request, HttpServletResponse response, User user) throws IOException {
        Integer projectId = parseId(request.getParameter("projectId")); Integer taskId = parseId(request.getParameter("taskId")); String status = ProjectSpacePolicy.taskStatus(request.getParameter("status"));
        boolean updated = projectId != null && taskId != null && status != null && projectDAO.updateTaskStatus(taskId, user.getId(), status);
        response.sendRedirect(request.getContextPath() + "/projects?id=" + (projectId == null ? "" : projectId) + (updated ? "&taskUpdated=1" : "&error=status"));
    }

    private User signedIn(HttpServletRequest request, HttpServletResponse response) throws IOException { User user = request.getSession(false) == null ? null : (User) request.getSession(false).getAttribute("user"); if (user == null) response.sendRedirect(request.getContextPath() + "/auth/signin"); return user; }
    private Integer parseId(String value) { try { int id = Integer.parseInt(value); return id > 0 ? id : null; } catch (NumberFormatException exception) { return null; } }
    private Date parseDate(String value) { try { return value == null || value.isBlank() ? null : Date.valueOf(LocalDate.parse(value)); } catch (Exception exception) { return null; } }
}
