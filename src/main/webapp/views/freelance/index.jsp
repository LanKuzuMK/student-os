<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Freelance Hub - Student OS</title>
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
                <a href="/skills/discover" class="nav-link">Discover</a>
                <a href="/skills" class="nav-link">My Skills</a>
            </div>
            <div class="nav-group">
                <div class="nav-title">Work</div>
                <a href="/freelance" class="nav-link" style="background-color: var(--border-color)">Freelance Jobs</a>
            </div>
            <div style="margin-top: auto;">
                <a href="/auth/logout" class="nav-link" style="color: var(--danger);">Logout</a>
            </div>
        </aside>

        <!-- Main Content -->
        <main class="main-content">
            <header class="page-header">
                <div>
                    <h1 class="page-title">Job Marketplace</h1>
                    <p class="page-subtitle">Turn your skills into opportunities.</p>
                </div>
                <button class="btn btn-primary" onclick="document.getElementById('jobModal').style.display='flex'">+ Post Job</button>
            </header>

            <section>
                <div class="grid-2">
                    <c:choose>
                        <c:when test="\">
                            <div style="grid-column: 1 / -1; padding: 40px; text-align: center; border: 1px dashed var(--border-color); border-radius: var(--radius); color: var(--text-secondary);">
                                No jobs available right now.
                            </div>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="job" items="\">
                                <div class="card">
                                    <div class="card-header">
                                        <div class="card-title">\</div>
                                        <span class="badge badge-completed">$\</span>
                                    </div>
                                    <p style="color: var(--text-secondary); font-size: 14px; margin-bottom: 16px;">\</p>
                                    <div style="font-size: 12px; color: var(--text-secondary); margin-bottom: 12px;">Status: \</div>
                                    <button class="btn btn-primary" style="width: 100%;">Apply</button>
                                </div>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </div>
            </section>
        </main>
    </div>

    <!-- Post Job Modal -->
    <div id="jobModal" style="display:none; position:fixed; top:0; left:0; width:100%; height:100%; background:rgba(0,0,0,0.5); align-items:center; justify-content:center;">
        <div style="background:var(--bg-primary); padding:32px; border-radius:12px; width:100%; max-width:500px;">
            <h2 style="margin-bottom:24px; font-size:20px;">Post a Job</h2>
            <form action="/freelance/post" method="POST">
                <div class="form-group">
                    <label class="form-label">Job Title</label>
                    <input type="text" name="title" class="form-control" required placeholder="Need a UI Designer">
                </div>
                <div class="form-group">
                    <label class="form-label">Description</label>
                    <textarea name="description" class="form-control" rows="4" required></textarea>
                </div>
                <div class="form-group">
                    <label class="form-label">Budget ($)</label>
                    <input type="number" step="0.01" name="budget" class="form-control" required placeholder="50.00">
                </div>
                <div style="display:flex; justify-content:flex-end; gap:12px; margin-top:24px;">
                    <button type="button" class="btn btn-secondary" onclick="document.getElementById('jobModal').style.display='none'">Cancel</button>
                    <button type="submit" class="btn btn-primary">Post Job</button>
                </div>
            </form>
        </div>
    </div>
</body>
</html>
