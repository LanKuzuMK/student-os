<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0"><title>Freelance Jobs - StudentOS</title>
    <link rel="stylesheet" href="/css/main.css?v=polish-20260822"><link rel="icon" type="image/png" href="/favicon.png">
</head>
<body>
<div class="app-container">
    <aside class="sidebar">
        <div class="sidebar-logo"><span class="logo-mark">S</span> Student OS</div>
        <div class="nav-group"><div class="nav-title">Workspace</div><a href="/dashboard" class="nav-link">Overview</a><a href="/goals" class="nav-link">Goals</a><a href="/schedule" class="nav-link">Schedule</a></div>
        <div class="nav-group"><div class="nav-title">Community</div><a href="/skills/discover" class="nav-link">Discover talent</a><a href="/skills" class="nav-link">My skills</a><a href="/collaborations" class="nav-link">Collaborations</a><a href="/saved" class="nav-link">Saved items</a><a href="/messages" class="nav-link">Messages<c:if test="${unreadMessageCount gt 0}"><span class="notification-badge"><c:out value="${unreadMessageCount}"/></span></c:if></a><a href="/notifications" class="nav-link">Notifications<c:if test="${unreadNotificationCount gt 0}"><span class="notification-badge"><c:out value="${unreadNotificationCount}"/></span></c:if></a><a href="/profile" class="nav-link">My profile</a></div>
        <div class="nav-group"><div class="nav-title">Opportunities</div><a href="/freelance" class="nav-link active">Freelance jobs</a><a href="/freelance/services" class="nav-link">Services</a></div>
        <div class="sidebar-footer"><a href="/auth/logout" class="nav-link nav-link-danger">Log out</a></div>
    </aside>
    <main class="main-content">
        <header class="page-header"><div><p class="eyebrow">Student marketplace</p><h1 class="page-title">Freelance jobs</h1><p class="page-subtitle">Find small opportunities, see who posted them, and contact the right student with context.</p></div><button class="btn btn-primary" type="button" onclick="document.getElementById('jobModal').style.display='flex'">Post a job</button></header>
        <c:if test="${param.error eq 'invalid'}"><div class="alert alert-error">Enter a title, description, and a valid non-negative budget.</div></c:if>
        <c:if test="${param.report eq '1'}"><div class="alert alert-success">Thank you. Your report is now in the StudentOS review queue.</div></c:if><c:if test="${param.report eq '0'}"><div class="alert alert-error">That report could not be submitted. You can report each item once while it is open.</div></c:if>
        <section class="grid-2">
            <c:choose>
                <c:when test="${empty jobs}"><div class="card">No jobs are available right now.</div></c:when>
                <c:otherwise>
                    <c:forEach var="job" items="${jobs}">
                        <article class="card skill-owner-card">
                            <div class="card-header"><div class="card-title"><c:out value="${job.title}"/></div><span class="badge badge-completed">$<c:out value="${job.budget}"/></span></div>
                            <p style="color:var(--text-secondary)"><c:out value="${job.description}"/></p>
                            <p class="owner-meta">Posted by <strong><c:out value="${job.ownerName}"/></strong> · <c:out value="${job.status}"/></p>
                            <div class="owner-actions opportunity-primary-actions">
                                <a href="/profile/view?id=<c:out value='${job.userId}'/>" class="btn btn-secondary">View profile</a>
                                <c:if test="${job.userId ne currentUserId}"><a href="/messages?toEmail=<c:out value='${job.ownerEmail}'/>" class="btn btn-primary">Message owner</a></c:if>
                            </div>
                            <c:if test="${job.userId ne currentUserId}"><div class="opportunity-secondary-actions"><span class="opportunity-utility-label">Keep or flag</span><div class="opportunity-utility-actions"><form action="/saved/add" method="post"><input type="hidden" name="csrfToken" value="<c:out value='${csrfToken}'/>"><input type="hidden" name="targetType" value="JOB"><input type="hidden" name="targetId" value="<c:out value='${job.id}'/>"><input type="hidden" name="returnTo" value="/freelance"><button class="opportunity-utility" type="submit">Save job</button></form><details class="opportunity-report"><summary class="opportunity-utility">Report</summary><div class="opportunity-report-popover"><form action="/reports/new" method="post"><input type="hidden" name="csrfToken" value="<c:out value='${csrfToken}'/>"><input type="hidden" name="targetType" value="JOB"><input type="hidden" name="targetId" value="<c:out value='${job.id}'/>"><input type="hidden" name="returnTo" value="/freelance"><label class="form-label" for="reportReason-<c:out value='${job.id}'/>">Reason</label><select id="reportReason-<c:out value='${job.id}'/>" name="reason" required><option value="SPAM">Spam</option><option value="HARASSMENT">Harassment</option><option value="INAPPROPRIATE">Inappropriate</option><option value="MISLEADING">Misleading</option><option value="OTHER">Other</option></select><input name="details" maxlength="1000" placeholder="Optional context"><button class="text-action text-action-danger" type="submit">Submit report</button></form></div></details></div></div></c:if>
                        </article>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </section>
        <footer class="mkv-footer">© 2026 MKV Team</footer>
    </main>
</div>
<div id="jobModal" class="profile-modal" style="display:none"><div class="profile-modal-card"><div class="profile-modal-heading"><div><p class="section-kicker">New opportunity</p><h2>Post a freelance job</h2></div><button type="button" class="modal-close" onclick="document.getElementById('jobModal').style.display='none'">Close</button></div><form action="/freelance/post" method="post"><input type="hidden" name="csrfToken" value="<c:out value='${csrfToken}'/>"><div class="form-group"><label class="form-label" for="jobTitle">Job title</label><input id="jobTitle" name="title" class="form-control" required></div><div class="form-group"><label class="form-label" for="jobDescription">Description</label><textarea id="jobDescription" name="description" class="form-control" rows="4" required></textarea></div><div class="form-group"><label class="form-label" for="jobBudget">Budget ($)</label><input id="jobBudget" type="number" min="0" step="0.01" name="budget" class="form-control" required></div><button class="btn btn-primary" type="submit">Post job</button></form></div></div>
    <script src="/js/mobile-nav.js?v=mobile-nav-phone-5" defer></script>
</body>
</html>
