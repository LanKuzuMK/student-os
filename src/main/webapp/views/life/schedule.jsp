<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Schedule - StudentOS</title>
    <link rel="stylesheet" href="/css/main.css?v=schedule-tasks-2">
    <link rel="icon" type="image/png" href="/favicon.png">
</head>
<body>
<div class="app-container">
    <aside class="sidebar">
        <div class="sidebar-logo">STUDENT OS</div>
        <div class="nav-group"><div class="nav-title">My Life</div>
            <a href="/dashboard" class="nav-link">Overview</a><a href="/goals" class="nav-link">Goals</a><a href="/schedule" class="nav-link active">Schedule</a>
        </div>
        <div class="nav-group"><div class="nav-title">Skills</div>
            <a href="/skills/discover" class="nav-link">Discover</a><a href="/skills" class="nav-link">My Skills</a><a href="/messages" class="nav-link">Messages</a><a href="/profile" class="nav-link">My profile</a>
        </div>
        <div class="nav-group"><div class="nav-title">Work</div>
            <a href="/freelance" class="nav-link">Freelance Jobs</a><a href="/freelance/services" class="nav-link">Services</a>
        </div>
        <div class="sidebar-footer"><a href="/auth/logout" class="nav-link nav-link-danger">Log out</a></div>
    </aside>

    <main class="main-content">
        <header class="page-header">
            <div><div class="eyebrow">Your workload</div><h1 class="page-title">Schedule</h1><p class="page-subtitle">Review your tasks, finish what is in progress, and clear completed work when you are done with it.</p></div>
            <a class="btn btn-secondary" href="/dashboard">Add a task</a>
        </header>
        <c:if test="${not empty param.success}"><div class="alert alert-success"><c:out value="${param.success}"/></div></c:if>
        <c:if test="${not empty param.error}"><div class="alert alert-error"><c:out value="${param.error}"/></div></c:if>

        <section class="schedule-manager" aria-labelledby="schedule-list-heading">
            <div class="schedule-manager-heading"><div><div class="section-kicker">Task list</div><h2 id="schedule-list-heading">Your scheduled work</h2></div><span>Completed tasks can be removed</span></div>
            <c:choose>
                <c:when test="${empty tasks}">
                    <div class="schedule-empty"><h3>No scheduled tasks yet.</h3><p>Create a task from your dashboard and it will appear here.</p><a href="/dashboard" class="btn btn-secondary">Go to dashboard</a></div>
                </c:when>
                <c:otherwise>
                    <div class="schedule-task-grid">
                        <c:forEach var="task" items="${tasks}">
                            <article class="schedule-task-card <c:if test='${task.status eq "COMPLETED"}'>is-completed</c:if>">
                                <div class="schedule-task-top"><span class="schedule-priority"><c:out value="${task.priority}"/></span><span class="schedule-status"><c:choose><c:when test="${task.status eq 'COMPLETED'}">Completed</c:when><c:otherwise>In progress</c:otherwise></c:choose></span></div>
                                <h3><c:out value="${task.title}"/></h3>
                                <p><c:choose><c:when test="${empty task.description}">No description added.</c:when><c:otherwise><c:out value="${task.description}"/></c:otherwise></c:choose></p>
                                <div class="schedule-task-footer">
                                    <c:choose>
                                        <c:when test="${task.status eq 'COMPLETED'}">
                                            <span class="completed-note">Finished work</span>
                                            <form action="/tasks/delete" method="post" onsubmit="return confirm('Delete this completed task? This cannot be undone.');"><input type="hidden" name="id" value="<c:out value='${task.id}'/>"><input type="hidden" name="returnTo" value="schedule"><button class="text-action text-action-danger" type="submit">Delete task</button></form>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="active-note">Finish this task from Dashboard</span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </article>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </section>
        <footer class="mkv-footer">© 2026 MKV Team</footer>
    </main>
</div>
    <script src="/js/mobile-nav.js" defer></script>
</body>
</html>
