<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - Student OS</title>
    <link rel="stylesheet" href="/css/main.css">
</head>
<body>
<div class="app-container">
    <aside class="sidebar">
        <div class="sidebar-logo">STUDENT OS</div>
        <div class="nav-group">
            <div class="nav-title">My Life</div>
            <a href="/dashboard" class="nav-link" style="background-color: var(--border-color)">Overview</a>
            <a href="/goals" class="nav-link">Goals</a>
            <a href="/schedule" class="nav-link">Schedule</a>
        </div>
        <div class="nav-group">
            <div class="nav-title">Skills</div>
            <a href="/skills/discover" class="nav-link">Discover</a>
            <a href="/skills" class="nav-link">My Skills</a>
        </div>
        <div class="nav-group">
            <div class="nav-title">Work</div>
            <a href="/freelance" class="nav-link">Freelance Jobs</a>
            <a href="/freelance/services" class="nav-link">Services</a>
        </div>
        <div style="margin-top: auto;">
            <div class="nav-link" style="color: var(--text-secondary); font-size: 12px; margin-bottom: 8px;">
                Logged in as <c:out value="${sessionScope.user.email}"/>
            </div>
            <a href="/auth/logout" class="nav-link" style="color: var(--danger);">Logout</a>
        </div>
    </aside>

    <main class="main-content">
        <header class="page-header">
            <div>
                <h1 class="page-title">Good morning.</h1>
                <p class="page-subtitle">Here's what matters today.</p>
            </div>
            <button class="btn btn-primary" type="button" onclick="document.getElementById('taskModal').style.display='flex'">+ New Task</button>
        </header>

        <section>
            <h2 style="font-size: 18px; margin-bottom: 16px; font-weight: 600;">Your Tasks</h2>
            <div class="grid-2">
                <c:choose>
                    <c:when test="${empty tasks}">
                        <div style="grid-column: 1 / -1; padding: 40px; text-align: center; border: 1px dashed var(--border-color); border-radius: var(--radius); color: var(--text-secondary);">
                            No tasks yet. Create one to get started.
                        </div>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="task" items="${tasks}">
                            <div class="card">
                                <div class="card-header">
                                    <div class="card-title"><c:out value="${task.title}"/></div>
                                    <c:choose>
                                        <c:when test="${task.status eq 'COMPLETED'}">
                                            <span class="badge badge-completed">Completed</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge badge-todo"><c:out value="${task.status}"/></span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                                <p style="color: var(--text-secondary); font-size: 14px; margin-bottom: 16px;"><c:out value="${task.description}"/></p>
                                <c:if test="${task.status ne 'COMPLETED'}">
                                    <form action="/tasks/complete" method="post" style="display:inline;">
                                        <input type="hidden" name="id" value="<c:out value='${task.id}'/>">
                                        <button type="submit" class="btn btn-secondary" style="font-size: 12px; padding: 4px 10px;">Mark Done</button>
                                    </form>
                                </c:if>
                            </div>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </div>
        </section>
    </main>
</div>

<div id="taskModal" style="display:none; position:fixed; top:0; left:0; width:100%; height:100%; background:rgba(0,0,0,0.5); align-items:center; justify-content:center;">
    <div style="background:var(--bg-primary); padding:32px; border-radius:12px; width:100%; max-width:400px;">
        <h2 style="margin-bottom:24px; font-size:20px;">Create Task</h2>
        <form action="/tasks/create" method="post">
            <div class="form-group">
                <label class="form-label" for="taskTitle">Title</label>
                <input id="taskTitle" type="text" name="title" class="form-control" required>
            </div>
            <div class="form-group">
                <label class="form-label" for="taskDescription">Description</label>
                <textarea id="taskDescription" name="description" class="form-control" rows="3"></textarea>
            </div>
            <div class="form-group">
                <label class="form-label" for="taskPriority">Priority</label>
                <select id="taskPriority" name="priority" class="form-control">
                    <option value="LOW">Low</option>
                    <option value="MEDIUM" selected>Medium</option>
                    <option value="HIGH">High</option>
                    <option value="URGENT">Urgent</option>
                </select>
            </div>
            <div style="display:flex; justify-content:flex-end; gap:12px; margin-top:24px;">
                <button type="button" class="btn btn-secondary" onclick="document.getElementById('taskModal').style.display='none'">Cancel</button>
                <button type="submit" class="btn btn-primary">Save Task</button>
            </div>
        </form>
    </div>
</div>
</body>
</html>
