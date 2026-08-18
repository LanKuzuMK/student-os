<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Dashboard - Student OS</title>
    <link rel="stylesheet" href="/css/main.css">
</head>
<body>
    <div class="app-container">
        <!-- Sidebar -->
        <aside class="sidebar">
            <div class="sidebar-logo">STUDENT OS</div>
            
            <div class="nav-group">
                <div class="nav-title">My Life</div>
                <a href="/dashboard" class="nav-link" style="background-color: var(--border-color)">Overview</a>
                <a href="#" class="nav-link">Goals</a>
                <a href="#" class="nav-link">Schedule</a>
            </div>
            
            <div class="nav-group">
                <div class="nav-title">Skills</div>
                <a href="#" class="nav-link">Discover</a>
                <a href="#" class="nav-link">My Skills</a>
            </div>
            
            <div class="nav-group">
                <div class="nav-title">Work</div>
                <a href="#" class="nav-link">Freelance Jobs</a>
                <a href="#" class="nav-link">Services</a>
            </div>

            <div style="margin-top: auto;">
                <div class="nav-link" style="color: var(--text-secondary); font-size: 12px; margin-bottom: 8px;">Logged in as \</div>
                <a href="/auth/logout" class="nav-link" style="color: var(--danger);">Logout</a>
            </div>
        </aside>

        <!-- Main Content -->
        <main class="main-content">
            <header class="page-header">
                <div>
                    <h1 class="page-title">Good morning.</h1>
                    <p class="page-subtitle">Here's what matters today.</p>
                </div>
                <button class="btn btn-primary" onclick="document.getElementById('taskModal').style.display='flex'">+ New Task</button>
            </header>

            <section>
                <h2 style="font-size: 18px; margin-bottom: 16px; font-weight: 600;">Your Tasks</h2>
                <div class="grid-2">
                    <c:choose>
                        <c:when test="\">
                            <div style="grid-column: 1 / -1; padding: 40px; text-align: center; border: 1px dashed var(--border-color); border-radius: var(--radius); color: var(--text-secondary);">
                                No tasks yet. Create one to get started.
                            </div>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="task" items="\">
                                <div class="card">
                                    <div class="card-header">
                                        <div class="card-title">\</div>
                                        <c:choose>
                                            <c:when test="\">
                                                <span class="badge badge-completed">Completed</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge badge-todo">\</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                    <p style="color: var(--text-secondary); font-size: 14px; margin-bottom: 16px;">\</p>
                                    <c:if test="\">
                                        <form action="/tasks/complete" method="POST" style="display:inline;">
                                            <input type="hidden" name="id" value="\">
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

    <!-- Modal (Simple CSS implementation) -->
    <div id="taskModal" style="display:none; position:fixed; top:0; left:0; width:100%; height:100%; background:rgba(0,0,0,0.5); align-items:center; justify-content:center;">
        <div style="background:var(--bg-primary); padding:32px; border-radius:12px; width:100%; max-width:400px;">
            <h2 style="margin-bottom:24px; font-size:20px;">Create Task</h2>
            <form action="/tasks/create" method="POST">
                <div class="form-group">
                    <label class="form-label">Title</label>
                    <input type="text" name="title" class="form-control" required>
                </div>
                <div class="form-group">
                    <label class="form-label">Description</label>
                    <textarea name="description" class="form-control" rows="3"></textarea>
                </div>
                <div class="form-group">
                    <label class="form-label">Priority</label>
                    <select name="priority" class="form-control">
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
