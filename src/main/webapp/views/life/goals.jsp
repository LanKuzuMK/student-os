<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Goals - Student OS</title>
    <link rel="stylesheet" href="/css/main.css">
    <link rel="icon" type="image/png" href="/favicon.png">
</head>
<body>
<div class="app-container">
    <aside class="sidebar">
        <div class="sidebar-logo">STUDENT OS</div>
        <div class="nav-group"><div class="nav-title">My Life</div>
            <a href="/dashboard" class="nav-link">Overview</a><a href="/goals" class="nav-link" style="background-color:var(--border-color)">Goals</a><a href="/schedule" class="nav-link">Schedule</a>
        </div>
        <div class="nav-group"><div class="nav-title">Skills</div>
            <a href="/skills/discover" class="nav-link">Discover</a><a href="/skills" class="nav-link">My Skills</a>
        </div>
        <div class="nav-group"><div class="nav-title">Work</div>
            <a href="/freelance" class="nav-link">Freelance Jobs</a><a href="/freelance/services" class="nav-link">Services</a>
        </div>
        <div style="margin-top:auto;"><a href="/auth/logout" class="nav-link" style="color:var(--danger)">Logout</a></div>
    </aside>
    <main class="main-content">
        <header class="page-header"><div><h1 class="page-title">Goals</h1><p class="page-subtitle">Track your learning and personal milestones.</p></div></header>
        <section class="card" style="margin-bottom:20px;">
            <form action="/goals" method="post">
                <div class="form-group"><label class="form-label" for="goalTitle">New goal</label><input id="goalTitle" class="form-control" name="title" required></div>
                <div class="form-group"><label class="form-label" for="goalDescription">Description</label><textarea id="goalDescription" class="form-control" name="description" rows="3"></textarea></div>
                <div class="form-group"><label class="form-label" for="goalProgress">Progress</label><input id="goalProgress" class="form-control" type="number" name="progress" min="0" max="100" value="0" required></div>
                <button class="btn btn-primary" type="submit">Add Goal</button>
            </form>
        </section>
        <section class="grid-2">
            <c:choose>
                <c:when test="${empty goals}"><div class="card">No goals yet. Add your first goal above.</div></c:when>
                <c:otherwise><c:forEach var="goal" items="${goals}">
                    <article class="card"><h3><c:out value="${goal.title}"/></h3><p style="color:var(--text-secondary)"><c:out value="${goal.description}"/></p><p><strong><c:out value="${goal.progress}"/>%</strong> complete</p></article>
                </c:forEach></c:otherwise>
            </c:choose>
        </section>
    </main>
<footer class="mkv-footer">© 2026 MKV Team</footer>
</div>
</body>
</html>
