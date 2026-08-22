package com.studentos.dao;

import com.studentos.model.ProjectMember;
import com.studentos.model.ProjectMilestone;
import com.studentos.model.ProjectSpace;
import com.studentos.model.ProjectTask;
import com.studentos.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Project Space data access: every query is membership- or owner-scoped. */
public class ProjectSpaceDAO {
    public Integer createProject(int ownerId, String title, String description) {
        String projectSql = "INSERT INTO project_spaces (owner_id, title, description) VALUES (?, ?, ?)";
        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement project = connection.prepareStatement(projectSql, Statement.RETURN_GENERATED_KEYS)) {
                project.setInt(1, ownerId); project.setString(2, title); project.setString(3, description); project.executeUpdate();
                try (ResultSet keys = project.getGeneratedKeys()) {
                    if (!keys.next()) { connection.rollback(); return null; }
                    int projectId = keys.getInt(1);
                    try (PreparedStatement member = connection.prepareStatement("INSERT INTO project_space_members (project_id, user_id, role) VALUES (?, ?, 'OWNER')")) {
                        member.setInt(1, projectId); member.setInt(2, ownerId); member.executeUpdate();
                    }
                    connection.commit(); return projectId;
                }
            } catch (SQLException exception) { connection.rollback(); throw exception; }
        } catch (SQLException exception) { System.err.println("Unable to create project space: " + exception.getMessage()); return null; }
    }

    public List<ProjectSpace> getForMember(int userId) {
        String sql = "SELECT p.*, m.role, (SELECT COUNT(*) FROM project_space_members pm WHERE pm.project_id = p.id) AS member_count, "
                + "(SELECT COUNT(*) FROM project_space_tasks pt WHERE pt.project_id = p.id AND pt.status <> 'COMPLETED') AS active_task_count, "
                + "COALESCE((SELECT AVG(CASE WHEN milestone.status = 'COMPLETED' THEN 100 ELSE 0 END)::int FROM project_space_milestones milestone WHERE milestone.project_id = p.id), 0) AS milestone_progress "
                + "FROM project_spaces p JOIN project_space_members m ON m.project_id = p.id WHERE m.user_id = ? ORDER BY p.created_at DESC";
        List<ProjectSpace> projects = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId); try (ResultSet result = statement.executeQuery()) { while (result.next()) projects.add(mapProject(result)); }
        } catch (SQLException exception) { System.err.println("Unable to load project spaces: " + exception.getMessage()); }
        return projects;
    }

    public ProjectSpace getForMember(int projectId, int userId) {
        for (ProjectSpace project : getForMember(userId)) if (project.getId() == projectId) return project;
        return null;
    }

    public List<ProjectMember> getMembers(int projectId, int requesterId) {
        if (!isMember(projectId, requesterId)) return List.of();
        String sql = "SELECT m.user_id, m.role, u.email, COALESCE(NULLIF(TRIM(CONCAT_WS(' ', p.first_name, p.last_name)), ''), u.email) AS display_name FROM project_space_members m JOIN users u ON u.id = m.user_id LEFT JOIN profiles p ON p.user_id = u.id WHERE m.project_id = ? ORDER BY CASE WHEN m.role = 'OWNER' THEN 0 ELSE 1 END, display_name";
        List<ProjectMember> members = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, projectId); try (ResultSet result = statement.executeQuery()) { while (result.next()) members.add(mapMember(result)); }
        } catch (SQLException exception) { System.err.println("Unable to load project members: " + exception.getMessage()); }
        return members;
    }

    public List<ProjectMember> getAcceptedCollaborators(int ownerId) {
        String sql = "SELECT DISTINCT u.id AS user_id, u.email, COALESCE(NULLIF(TRIM(CONCAT_WS(' ', p.first_name, p.last_name)), ''), u.email) AS display_name FROM collaboration_requests r JOIN users u ON u.id = CASE WHEN r.requester_id = ? THEN r.recipient_id ELSE r.requester_id END LEFT JOIN profiles p ON p.user_id = u.id WHERE (r.requester_id = ? OR r.recipient_id = ?) AND r.status = 'ACCEPTED' AND u.status = 'ACTIVE' ORDER BY display_name";
        List<ProjectMember> members = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, ownerId); statement.setInt(2, ownerId); statement.setInt(3, ownerId);
            try (ResultSet result = statement.executeQuery()) { while (result.next()) members.add(mapMember(result)); }
        } catch (SQLException exception) { System.err.println("Unable to load eligible collaborators: " + exception.getMessage()); }
        return members;
    }

    public boolean addAcceptedCollaborator(int projectId, int ownerId, int memberId, String role) {
        if (!isOwner(projectId, ownerId) || !hasAcceptedCollaboration(ownerId, memberId)) return false;
        String sql = "INSERT INTO project_space_members (project_id, user_id, role) VALUES (?, ?, ?) ON CONFLICT (project_id, user_id) DO NOTHING";
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, projectId); statement.setInt(2, memberId); statement.setString(3, role); return statement.executeUpdate() == 1;
        } catch (SQLException exception) { return false; }
    }

    public List<ProjectMilestone> getMilestones(int projectId, int requesterId) {
        if (!isMember(projectId, requesterId)) return List.of();
        String sql = "SELECT * FROM project_space_milestones WHERE project_id = ? ORDER BY CASE status WHEN 'IN_PROGRESS' THEN 0 WHEN 'TODO' THEN 1 ELSE 2 END, due_date NULLS LAST, id DESC";
        List<ProjectMilestone> milestones = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, projectId); try (ResultSet result = statement.executeQuery()) { while (result.next()) milestones.add(mapMilestone(result)); }
        } catch (SQLException exception) { System.err.println("Unable to load project milestones: " + exception.getMessage()); }
        return milestones;
    }

    public boolean addMilestone(int projectId, int ownerId, String title, String description, Date dueDate) {
        if (!isOwner(projectId, ownerId)) return false;
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO project_space_milestones (project_id, title, description, due_date) VALUES (?, ?, ?, ?)")) {
            statement.setInt(1, projectId); statement.setString(2, title); statement.setString(3, description); statement.setDate(4, dueDate); return statement.executeUpdate() == 1;
        } catch (SQLException exception) { return false; }
    }

    public List<ProjectTask> getTasks(int projectId, int requesterId) {
        if (!isMember(projectId, requesterId)) return List.of();
        String sql = "SELECT t.*, COALESCE(NULLIF(TRIM(CONCAT_WS(' ', p.first_name, p.last_name)), ''), u.email, 'Unassigned') AS assignee_name FROM project_space_tasks t LEFT JOIN users u ON u.id = t.assignee_id LEFT JOIN profiles p ON p.user_id = u.id WHERE t.project_id = ? ORDER BY CASE t.status WHEN 'IN_PROGRESS' THEN 0 WHEN 'TODO' THEN 1 WHEN 'BLOCKED' THEN 2 ELSE 3 END, CASE t.priority WHEN 'HIGH' THEN 0 WHEN 'MEDIUM' THEN 1 ELSE 2 END, t.id DESC";
        List<ProjectTask> tasks = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, projectId); try (ResultSet result = statement.executeQuery()) { while (result.next()) tasks.add(mapTask(result)); }
        } catch (SQLException exception) { System.err.println("Unable to load project tasks: " + exception.getMessage()); }
        return tasks;
    }

    public boolean addTask(int projectId, int memberId, String title, String description, String priority, Integer assigneeId) {
        if (!isMember(projectId, memberId) || (assigneeId != null && !isMember(projectId, assigneeId))) return false;
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO project_space_tasks (project_id, created_by, assignee_id, title, description, priority) VALUES (?, ?, ?, ?, ?, ?)")) {
            statement.setInt(1, projectId); statement.setInt(2, memberId); if (assigneeId == null) statement.setNull(3, Types.INTEGER); else statement.setInt(3, assigneeId); statement.setString(4, title); statement.setString(5, description); statement.setString(6, priority); return statement.executeUpdate() == 1;
        } catch (SQLException exception) { return false; }
    }

    public boolean updateTaskStatus(int taskId, int memberId, String status) {
        String sql = "UPDATE project_space_tasks t SET status = ? WHERE t.id = ? AND EXISTS (SELECT 1 FROM project_space_members m WHERE m.project_id = t.project_id AND m.user_id = ? AND (m.role = 'OWNER' OR t.assignee_id IS NULL OR t.assignee_id = ?))";
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status); statement.setInt(2, taskId); statement.setInt(3, memberId); statement.setInt(4, memberId); return statement.executeUpdate() == 1;
        } catch (SQLException exception) { return false; }
    }

    public boolean isMember(int projectId, int userId) { return hasProjectRole(projectId, userId, null); }
    public boolean isOwner(int projectId, int userId) { return hasProjectRole(projectId, userId, "OWNER"); }

    private boolean hasProjectRole(int projectId, int userId, String role) {
        String sql = "SELECT 1 FROM project_space_members WHERE project_id = ? AND user_id = ?" + (role == null ? "" : " AND role = ?");
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, projectId); statement.setInt(2, userId); if (role != null) statement.setString(3, role); try (ResultSet result = statement.executeQuery()) { return result.next(); }
        } catch (SQLException exception) { return false; }
    }

    private boolean hasAcceptedCollaboration(int firstId, int secondId) {
        String sql = "SELECT 1 FROM collaboration_requests WHERE status = 'ACCEPTED' AND ((requester_id = ? AND recipient_id = ?) OR (requester_id = ? AND recipient_id = ?))";
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, firstId); statement.setInt(2, secondId); statement.setInt(3, secondId); statement.setInt(4, firstId); try (ResultSet result = statement.executeQuery()) { return result.next(); }
        } catch (SQLException exception) { return false; }
    }

    private ProjectSpace mapProject(ResultSet result) throws SQLException { ProjectSpace project = new ProjectSpace(); project.setId(result.getInt("id")); project.setOwnerId(result.getInt("owner_id")); project.setTitle(result.getString("title")); project.setDescription(result.getString("description")); project.setStatus(result.getString("status")); project.setCreatedAt(result.getTimestamp("created_at")); project.setMemberRole(result.getString("role")); project.setMemberCount(result.getInt("member_count")); project.setActiveTaskCount(result.getInt("active_task_count")); project.setMilestoneProgress(result.getInt("milestone_progress")); return project; }
    private ProjectMember mapMember(ResultSet result) throws SQLException { ProjectMember member = new ProjectMember(); member.setUserId(result.getInt("user_id")); member.setRole(result.getString("role")); member.setDisplayName(result.getString("display_name")); member.setEmail(result.getString("email")); return member; }
    private ProjectMilestone mapMilestone(ResultSet result) throws SQLException { ProjectMilestone milestone = new ProjectMilestone(); milestone.setId(result.getInt("id")); milestone.setProjectId(result.getInt("project_id")); milestone.setTitle(result.getString("title")); milestone.setDescription(result.getString("description")); milestone.setStatus(result.getString("status")); milestone.setDueDate(result.getDate("due_date")); return milestone; }
    private ProjectTask mapTask(ResultSet result) throws SQLException { ProjectTask task = new ProjectTask(); task.setId(result.getInt("id")); task.setProjectId(result.getInt("project_id")); task.setTitle(result.getString("title")); task.setDescription(result.getString("description")); task.setStatus(result.getString("status")); task.setPriority(result.getString("priority")); int assignee = result.getInt("assignee_id"); task.setAssigneeId(result.wasNull() ? null : assignee); task.setAssigneeName(result.getString("assignee_name")); return task; }
}
