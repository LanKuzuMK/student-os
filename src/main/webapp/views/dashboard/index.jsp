<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="theme-color" content="#5a5ce2">
    <title>Dashboard - StudentOS</title>
    <link rel="icon" type="image/png" href="/favicon.png">
    <link rel="stylesheet" href="/css/main.css">
</head>
<body>
<c:set var="activeTasks" value="0"/>
<c:set var="completedTasks" value="0"/>
<c:forEach var="taskCounter" items="${tasks}">
    <c:choose>
        <c:when test="${taskCounter.status eq 'COMPLETED'}"><c:set var="completedTasks" value="${completedTasks + 1}"/></c:when>
        <c:otherwise><c:set var="activeTasks" value="${activeTasks + 1}"/></c:otherwise>
    </c:choose>
</c:forEach>
<div class="app-container">
    <aside class="sidebar">
        <div class="sidebar-logo">StudentOS</div>
        <div class="nav-group">
            <div class="nav-title">My Workspace</div>
            <a href="/dashboard" class="nav-link active">Overview</a>
            <a href="/goals" class="nav-link">Goals</a>
            <a href="/schedule" class="nav-link">Schedule</a>
        </div>
        <div class="nav-group">
            <div class="nav-title">Community</div>
            <a href="/skills/discover" class="nav-link">Discover talent</a>
            <a href="/skills" class="nav-link">My skills</a>
            <a href="/messages" class="nav-link">Messages</a>
        </div>
        <div class="nav-group">
            <div class="nav-title">Opportunity</div>
            <a href="/freelance" class="nav-link">Freelance jobs</a>
            <a href="/freelance/services" class="nav-link">Services</a>
        </div>
        <div class="sidebar-footer">
            <div class="account-chip"><c:out value="${sessionScope.user.email}"/></div>
            <a href="/auth/logout" class="nav-link nav-link-danger">Logout</a>
        </div>
    </aside>

    <main class="main-content">
        <div class="app-topbar">
            <div class="breadcrumb"><strong>StudentOS</strong> &nbsp;/&nbsp; Workspace</div>
            <div class="topbar-actions"><span class="topbar-date"><span class="status-dot"></span>Your workspace is ready</span></div>
        </div>

        <header class="page-header">
            <div>
                <div class="eyebrow">Your personal command center</div>
                <h1 class="page-title">Make today count.</h1>
                <p class="page-subtitle">Plan with intention, keep momentum, and turn every study session into visible progress.</p>
            </div>
            <button class="btn btn-primary" type="button" onclick="document.getElementById('taskModal').style.display='flex'">+ New task</button>
        </header>

        <section class="dashboard-hero" aria-label="StudentOS workspace overview">
            <div class="hero-content">
                <div class="hero-kicker">StudentOS Focus Mode</div>
                <h2 class="hero-title">Your next milestone starts with one focused move.</h2>
                <p class="hero-copy">Create a task, connect with talented students, or share the skills you are ready to offer. Your workspace keeps the important things moving.</p>
                <div class="hero-actions">
                    <button class="btn btn-hero" type="button" onclick="document.getElementById('taskModal').style.display='flex'">Plan a task</button>
                    <a class="btn btn-hero-quiet" href="/skills/discover">Discover students</a>
                </div>
            </div>
            <div class="hero-aside">
                <span class="hero-aside-label">Current momentum</span>
                <strong class="hero-aside-value"><c:out value="${activeTasks}"/> active task<c:if test="${activeTasks ne 1}">s</c:if></strong>
                <span class="hero-aside-note">One thoughtful action is enough to begin.</span>
            </div>
        </section>

        <section class="stats-grid" aria-label="Workspace statistics">
            <div class="stat-card">
                <div class="stat-icon stat-icon-blue">↗</div>
                <div>
                    <span class="stat-label">Active focus</span>
                    <strong class="stat-value"><c:out value="${activeTasks}"/> task<c:if test="${activeTasks ne 1}">s</c:if></strong>
                    <span class="stat-note">Ready for your attention</span>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-icon stat-icon-mint">✓</div>
                <div>
                    <span class="stat-label">Progress made</span>
                    <strong class="stat-value"><c:out value="${completedTasks}"/> complete</strong>
                    <span class="stat-note">Every finished task matters</span>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-icon stat-icon-amber">◎</div>
                <div>
                    <span class="stat-label">Community</span>
                    <strong class="stat-value">Skills hub</strong>
                    <span class="stat-note">Find your next collaborator</span>
                </div>
            </div>
        </section>

        <section class="dashboard-columns">
            <div class="panel">
                <div class="panel-heading">
                    <div>
                        <h2 class="panel-title">Your focus list</h2>
                        <p class="panel-subtitle">The tasks that keep your day moving forward.</p>
                    </div>
                    <a class="panel-link" href="/schedule">View schedule</a>
                </div>
                <div class="focus-list">
                    <c:choose>
                        <c:when test="${empty tasks}">
                            <div class="empty-focus"><strong>Your workspace is clear.</strong>Create your first task and make a small start today.</div>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="task" items="${tasks}">
                                <div class="focus-item <c:if test='${task.status eq "COMPLETED"}'>completed</c:if>">
                                    <span class="focus-indicator"></span>
                                    <div>
                                        <div class="focus-task-title"><c:out value="${task.title}"/></div>
                                        <div class="focus-task-copy"><c:out value="${task.description}" default="Add a short note to keep the task clear."/></div>
                                    </div>
                                    <c:choose>
                                        <c:when test="${task.status eq 'COMPLETED'}"><span class="focus-badge">Done</span></c:when>
                                        <c:otherwise>
                                            <form action="/tasks/complete" method="post">
                                                <input type="hidden" name="id" value="<c:out value='${task.id}'/>">
                                                <button type="submit" class="btn btn-secondary" style="min-height:32px;padding:6px 10px;font-size:11px;">Finish</button>
                                            </form>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <aside class="panel network-card">
                <div class="network-icon">✦</div>
                <h3>Talent is closer than you think.</h3>
                <p>Find peers who can share knowledge, exchange feedback, and collaborate on the next opportunity.</p>
                <a href="/skills/discover" class="btn btn-primary">Explore talent</a>
            </aside>
        </section>
    </main>
</div>

<div id="taskModal" style="display:none;position:fixed;z-index:20;inset:0;padding:20px;background:rgba(13,25,52,.48);align-items:center;justify-content:center;backdrop-filter:blur(5px);">
    <div style="width:100%;max-width:440px;padding:28px;background:#fff;border:1px solid #e5eaf3;border-radius:20px;box-shadow:0 28px 62px rgba(20,30,60,.25);">
        <div style="margin-bottom:22px;"><div class="section-kicker">Plan your next move</div><h2 style="font-family:'Plus Jakarta Sans',sans-serif;font-size:22px;letter-spacing:-.8px;">Create a focused task</h2></div>
        <form action="/tasks/create" method="post">
            <div class="form-group"><label class="form-label" for="taskTitle">Task title</label><input id="taskTitle" type="text" name="title" class="form-control" placeholder="e.g. Review Java Servlet notes" required></div>
            <div class="form-group"><label class="form-label" for="taskDescription">Short description</label><textarea id="taskDescription" name="description" class="form-control" rows="3" placeholder="What does done look like?"></textarea></div>
            <div class="form-group"><label class="form-label" for="taskPriority">Priority</label><select id="taskPriority" name="priority" class="form-control"><option value="LOW">Low</option><option value="MEDIUM" selected>Medium</option><option value="HIGH">High</option><option value="URGENT">Urgent</option></select></div>
            <div style="display:flex;justify-content:flex-end;gap:10px;margin-top:24px;"><button type="button" class="btn btn-secondary" onclick="document.getElementById('taskModal').style.display='none'">Cancel</button><button type="submit" class="btn btn-primary">Save task</button></div>
        </form>
    </div>
</div>
</body>
</html>
