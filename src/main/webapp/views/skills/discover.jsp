<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Discover Skills - Student OS</title>
    <link rel="stylesheet" href="/css/main.css">
</head>
<body>
    <div class="app-container">
        <!-- Sidebar -->
        <aside class="sidebar">
            <div class="sidebar-logo">STUDENT OS</div>
            <div class="nav-group">
                <div class="nav-title">My Life</div>
                <a href="/dashboard" class="nav-link">Overview</a>
            </div>
            <div class="nav-group">
                <div class="nav-title">Skills</div>
                <a href="/skills/discover" class="nav-link" style="background-color: var(--border-color)">Discover</a>
                <a href="/skills" class="nav-link">My Skills</a>
            </div>
            <div class="nav-group">
                <div class="nav-title">Work</div>
                <a href="/freelance" class="nav-link">Freelance Jobs</a>
            </div>
            <div style="margin-top: auto;">
                <a href="/auth/logout" class="nav-link" style="color: var(--danger);">Logout</a>
            </div>
        </aside>

        <main class="main-content">
            <header class="page-header">
                <div>
                    <h1 class="page-title">Discover Matches</h1>
                    <p class="page-subtitle">Find students to swap skills with.</p>
                </div>
            </header>

            <section>
                <div class="grid-2">
                    <c:choose>
                        <c:when test="\">
                            <div style="grid-column: 1 / -1; padding: 40px; text-align: center; border: 1px dashed var(--border-color); border-radius: var(--radius); color: var(--text-secondary);">
                                No skills exist in the community yet.
                            </div>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="skill" items="\">
                                <div class="card">
                                    <div class="card-header">
                                        <div class="card-title">User ID: \</div>
                                        <span class="badge badge-todo">\</span>
                                    </div>
                                    <h3 style="margin-bottom: 8px;">\</h3>
                                    <p style="color: var(--text-secondary); font-size: 14px; margin-bottom: 16px;">Level: \</p>
                                    <button class="btn btn-secondary" style="width: 100%;">Send Request</button>
                                </div>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </div>
            </section>
        </main>
    </div>
</body>
</html>
