<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0"><title>Freelance Jobs - StudentOS</title>
    <link rel="stylesheet" href="/css/main.css?v=mobile-nav-phone-4"><link rel="icon" type="image/png" href="/favicon.png">
</head>
<body>
<div class="app-container">
    <aside class="sidebar">
        <div class="sidebar-logo"><span class="logo-mark">S</span> Student OS</div>
        <div class="nav-group"><div class="nav-title">Workspace</div><a href="/dashboard" class="nav-link">Overview</a><a href="/goals" class="nav-link">Goals</a><a href="/schedule" class="nav-link">Schedule</a></div>
        <div class="nav-group"><div class="nav-title">Community</div><a href="/skills/discover" class="nav-link">Discover talent</a><a href="/skills" class="nav-link">My skills</a><a href="/messages" class="nav-link">Messages<c:if test="${unreadMessageCount gt 0}"><span class="notification-badge"><c:out value="${unreadMessageCount}"/></span></c:if></a><a href="/profile" class="nav-link">My profile</a></div>
        <div class="nav-group"><div class="nav-title">Opportunities</div><a href="/freelance" class="nav-link active">Freelance jobs</a><a href="/freelance/services" class="nav-link">Services</a></div>
        <div class="sidebar-footer"><a href="/auth/logout" class="nav-link nav-link-danger">Log out</a></div>
    </aside>
    <main class="main-content">
        <header class="page-header"><div><p class="eyebrow">Student marketplace</p><h1 class="page-title">Freelance jobs</h1><p class="page-subtitle">Find small opportunities, see who posted them, and contact the right student with context.</p></div><button class="btn btn-primary" type="button" onclick="document.getElementById('jobModal').style.display='flex'">Post a job</button></header>
        <section class="grid-2">
            <c:choose>
                <c:when test="${empty jobs}"><div class="card">No jobs are available right now.</div></c:when>
                <c:otherwise>
                    <c:forEach var="job" items="${jobs}">
                        <article class="card skill-owner-card">
                            <div class="card-header"><div class="card-title"><c:out value="${job.title}"/></div><span class="badge badge-completed">$<c:out value="${job.budget}"/></span></div>
                            <p style="color:var(--text-secondary)"><c:out value="${job.description}"/></p>
                            <p class="owner-meta">Posted by <strong><c:out value="${job.ownerName}"/></strong> · <c:out value="${job.status}"/></p>
                            <div class="owner-actions">
                                <a href="/profile/view?id=<c:out value='${job.userId}'/>" class="btn btn-secondary">View profile</a>
                                <c:if test="${job.userId ne currentUserId}"><a href="/messages?toEmail=<c:out value='${job.ownerEmail}'/>" class="btn btn-primary">Message owner</a></c:if>
                            </div>
                        </article>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </section>
        <footer class="mkv-footer">© 2026 MKV Team</footer>
    </main>
</div>
<div id="jobModal" class="profile-modal" style="display:none"><div class="profile-modal-card"><div class="profile-modal-heading"><div><p class="section-kicker">New opportunity</p><h2>Post a freelance job</h2></div><button type="button" class="modal-close" onclick="document.getElementById('jobModal').style.display='none'">Close</button></div><form action="/freelance/post" method="post"><div class="form-group"><label class="form-label" for="jobTitle">Job title</label><input id="jobTitle" name="title" class="form-control" required></div><div class="form-group"><label class="form-label" for="jobDescription">Description</label><textarea id="jobDescription" name="description" class="form-control" rows="4" required></textarea></div><div class="form-group"><label class="form-label" for="jobBudget">Budget ($)</label><input id="jobBudget" type="number" step="0.01" name="budget" class="form-control" required></div><button class="btn btn-primary" type="submit">Post job</button></form></div></div>
    <script src="/js/mobile-nav.js?v=mobile-nav-phone-4" defer></script>
</body>
</html>
