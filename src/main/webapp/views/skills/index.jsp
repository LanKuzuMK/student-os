<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>My Skills - Student OS</title>
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
                <a href="/skills" class="nav-link" style="background-color: var(--border-color)">My Skills</a>
            </div>
            <div class="nav-group">
                <div class="nav-title">Work</div>
                <a href="/freelance" class="nav-link">Freelance Jobs</a>
            </div>
            <div style="margin-top: auto;">
                <a href="/admin" class="nav-link">Admin Panel</a>
                <a href="/auth/logout" class="nav-link" style="color: var(--danger);">Logout</a>
            </div>
        </aside>

        <!-- Main Content -->
        <main class="main-content">
            <header class="page-header">
                <div>
                    <h1 class="page-title">My Skills</h1>
                    <p class="page-subtitle">Manage what you know and what you want to learn.</p>
                </div>
                <button class="btn btn-primary" onclick="document.getElementById('skillModal').style.display='flex'">+ Add Skill</button>
            </header>

            <section>
                <div class="grid-2">
                    <c:choose>
                        <c:when test="\">
                            <div style="grid-column: 1 / -1; padding: 40px; text-align: center; border: 1px dashed var(--border-color); border-radius: var(--radius); color: var(--text-secondary);">
                                No skills added yet. Add skills to your profile and we'll find compatible people.
                            </div>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="skill" items="\">
                                <div class="card">
                                    <div class="card-header">
                                        <div class="card-title">\</div>
                                        <span class="badge \">\</span>
                                    </div>
                                    <p style="color: var(--text-secondary); font-size: 14px;">Level: \</p>
                                </div>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </div>
            </section>
        </main>
    </div>

    <!-- Add Skill Modal -->
    <div id="skillModal" style="display:none; position:fixed; top:0; left:0; width:100%; height:100%; background:rgba(0,0,0,0.5); align-items:center; justify-content:center;">
        <div style="background:var(--bg-primary); padding:32px; border-radius:12px; width:100%; max-width:400px;">
            <h2 style="margin-bottom:24px; font-size:20px;">Add Skill</h2>
            <form action="/skills/add" method="POST">
                <div class="form-group">
                    <label class="form-label">Skill Name (e.g. Java, UI/UX)</label>
                    <input type="text" name="skillName" class="form-control" required>
                </div>
                <div class="form-group">
                    <label class="form-label">Skill Level</label>
                    <select name="skillLevel" class="form-control">
                        <option value="BEGINNER">Beginner</option>
                        <option value="INTERMEDIATE">Intermediate</option>
                        <option value="ADVANCED">Advanced</option>
                        <option value="EXPERT">Expert</option>
                    </select>
                </div>
                <div class="form-group">
                    <label class="form-label">I want to...</label>
                    <select name="type" class="form-control">
                        <option value="TEACH">Teach this skill</option>
                        <option value="LEARN">Learn this skill</option>
                    </select>
                </div>
                <div style="display:flex; justify-content:flex-end; gap:12px; margin-top:24px;">
                    <button type="button" class="btn btn-secondary" onclick="document.getElementById('skillModal').style.display='none'">Cancel</button>
                    <button type="submit" class="btn btn-primary">Save Skill</button>
                </div>
            </form>
        </div>
    </div>
</body>
</html>
