<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0"><title>Discover Skills - StudentOS</title>
    <link rel="stylesheet" href="/css/main.css?v=mobile-nav-phone-5"><link rel="icon" type="image/png" href="/favicon.png">
</head>
<body>
<div class="app-container">
    <aside class="sidebar">
        <div class="sidebar-logo"><span class="logo-mark">S</span> Student OS</div>
        <div class="nav-group"><div class="nav-title">Workspace</div><a href="/dashboard" class="nav-link">Overview</a><a href="/goals" class="nav-link">Goals</a><a href="/schedule" class="nav-link">Schedule</a></div>
        <div class="nav-group"><div class="nav-title">Community</div><a href="/skills/discover" class="nav-link active">Discover talent</a><a href="/skills" class="nav-link">My skills</a><a href="/messages" class="nav-link">Messages<c:if test="${unreadMessageCount gt 0}"><span class="notification-badge"><c:out value="${unreadMessageCount}"/></span></c:if></a><a href="/profile" class="nav-link">My profile</a></div>
        <div class="nav-group"><div class="nav-title">Opportunities</div><a href="/freelance" class="nav-link">Freelance jobs</a><a href="/freelance/services" class="nav-link">Services</a></div>
        <div class="sidebar-footer"><a href="/auth/logout" class="nav-link nav-link-danger">Log out</a></div>
    </aside>
    <main class="main-content">
        <header class="page-header"><div><p class="eyebrow">Student network</p><h1 class="page-title">Discover talent</h1><p class="page-subtitle">Explore what classmates are ready to teach or learn, then see who is behind the skill.</p></div><a href="/profile" class="btn btn-secondary">Complete my profile</a></header>
        <section class="grid-2">
            <c:choose>
                <c:when test="${empty allSkills}"><div class="card">No skills exist in the community yet.</div></c:when>
                <c:otherwise>
                    <c:forEach var="skill" items="${allSkills}">
                        <article class="card skill-owner-card">
                            <div class="card-header"><div class="card-title"><c:out value="${skill.skillName}"/></div><span class="badge badge-todo"><c:out value="${skill.type}"/></span></div>
                            <p style="color:var(--text-secondary)">Level: <c:out value="${skill.skillLevel}"/></p>
                            <p class="owner-meta">Shared by <strong><c:out value="${skill.ownerName}"/></strong></p>
                            <div class="owner-actions">
                                <a href="/profile/view?id=<c:out value='${skill.userId}'/>" class="btn btn-secondary">View profile</a>
                                <c:if test="${skill.userId ne currentUserId}"><a href="/messages?toEmail=<c:out value='${skill.ownerEmail}'/>" class="btn btn-primary">Message</a></c:if>
                            </div>
                        </article>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </section>
        <footer class="mkv-footer">© 2026 MKV Team</footer>
    </main>
</div>
    <script src="/js/mobile-nav.js?v=mobile-nav-phone-5" defer></script>
</body>
</html>
