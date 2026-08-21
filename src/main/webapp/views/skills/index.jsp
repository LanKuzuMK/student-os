<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Skills - StudentOS</title>
    <link rel="stylesheet" href="/css/main.css">
    <link rel="icon" type="image/png" href="/favicon.png">
</head>
<body>
<div class="app-container">
    <aside class="sidebar">
        <div class="sidebar-logo">StudentOS</div>
        <div class="nav-group">
            <div class="nav-title">My Workspace</div>
            <a href="/dashboard" class="nav-link">Overview</a>
            <a href="/goals" class="nav-link">Goals</a>
            <a href="/schedule" class="nav-link">Schedule</a>
        </div>
        <div class="nav-group">
            <div class="nav-title">Community</div>
            <a href="/skills/discover" class="nav-link">Discover talent</a>
            <a href="/skills" class="nav-link active">My skills</a>
            <a href="/messages" class="nav-link">Messages<c:if test="${unreadMessageCount gt 0}"><span class="notification-badge"><c:out value="${unreadMessageCount}"/></span></c:if></a><a href="/profile" class="nav-link">My profile</a>
        </div>
        <div class="nav-group">
            <div class="nav-title">Opportunity</div>
            <a href="/freelance" class="nav-link">Freelance jobs</a>
            <a href="/freelance/services" class="nav-link">Services</a>
        </div>
        <div class="sidebar-footer"><a href="/auth/logout" class="nav-link nav-link-danger">Logout</a></div>
    </aside>

    <main class="main-content">
        <header class="page-header">
            <div>
                <div class="eyebrow">Your learning profile</div>
                <h1 class="page-title">My Skills</h1>
                <p class="page-subtitle">Manage what you know, what you want to learn, and how you would like to collaborate.</p>
            </div>
            <button class="btn btn-primary" type="button" onclick="document.getElementById('skillModal').style.display='flex'">+ Add skill</button>
        </header>
        <section class="grid-2">
            <c:choose>
                <c:when test="${empty mySkills}"><div class="card">No skills yet. Add a skill to start building your profile.</div></c:when>
                <c:otherwise>
                    <c:forEach var="skill" items="${mySkills}">
                        <article class="card">
                            <div class="card-header"><div class="card-title"><c:out value="${skill.skillName}"/></div><span class="badge badge-todo"><c:out value="${skill.type}"/></span></div>
                            <p style="color:var(--text-secondary)">Level: <c:out value="${skill.skillLevel}"/></p>
                        </article>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </section>
    <footer class="mkv-footer">© 2026 MKV Team</footer>
    </main>
</div>

<div id="skillModal" class="modal-backdrop" style="display:none;" role="dialog" aria-modal="true" aria-labelledby="skillModalTitle">
    <div class="modal-card">
        <div class="section-kicker">Build your profile</div>
        <h2 id="skillModalTitle">Add a skill</h2>
        <form action="/skills/add" method="post">
            <div class="form-group" style="margin-top:22px;"><label class="form-label" for="skillName">Skill name</label><input id="skillName" class="form-control" name="skillName" placeholder="e.g. Java, UI design, public speaking" required></div>
            <div class="form-group"><label class="form-label" for="skillLevel">Skill level</label><select id="skillLevel" class="form-control" name="skillLevel"><option value="BEGINNER">Beginner</option><option value="INTERMEDIATE">Intermediate</option><option value="ADVANCED">Advanced</option><option value="EXPERT">Expert</option></select></div>
            <div class="form-group"><label class="form-label" for="skillType">I want to</label><select id="skillType" class="form-control" name="type"><option value="TEACH">Teach this skill</option><option value="LEARN">Learn this skill</option></select></div>
            <div class="modal-actions"><button class="btn btn-secondary" type="button" onclick="document.getElementById('skillModal').style.display='none'">Cancel</button><button class="btn btn-primary" type="submit">Save skill</button></div>
        </form>
    </div>
</div>
    <script src="/js/mobile-nav.js" defer></script>
</body>
</html>
