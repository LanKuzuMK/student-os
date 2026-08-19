<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Schedule - Student OS</title>
    <link rel="stylesheet" href="/css/main.css">
    <link rel="icon" type="image/png" href="/favicon.png">
</head>
<body>
<div class="app-container">
    <aside class="sidebar">
        <div class="sidebar-logo">STUDENT OS</div>
        <div class="nav-group">
            <div class="nav-title">My Life</div>
            <a href="/dashboard" class="nav-link">Overview</a>
            <a href="/goals" class="nav-link">Goals</a>
            <a href="/schedule" class="nav-link" style="background-color: var(--border-color)">Schedule</a>
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
        <div style="margin-top:auto;">
            <a href="/auth/logout" class="nav-link" style="color:var(--danger);">Logout</a>
        </div>
    </aside>
    <main class="main-content">
        <header class="page-header">
            <div>
                <h1 class="page-title">Schedule</h1>
                <p class="page-subtitle">Your current tasks and priorities.</p>
            </div>
        </header>
        <section>
            <div class="grid-2">
                <c:choose>
                    <c:when test="${empty tasks}">
                        <div class="card">No scheduled tasks yet. Add a task from the dashboard to see it here.</div>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="task" items="${tasks}">
                            <article class="card">
                                <div class="card-header">
                                    <div class="card-title"><c:out value="${task.title}"/></div>
                                    <span class="badge badge-todo"><c:out value="${task.priority}"/></span>
                                </div>
                                <p style="color:var(--text-secondary);"><c:out value="${task.description}"/></p>
                                <small>Status: <c:out value="${task.status}"/></small>
                            </article>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </div>
        </section>
    </main>
</div>
</body>
</html>
