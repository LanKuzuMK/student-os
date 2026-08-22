<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0"><title>Discover Skills - StudentOS</title>
    <link rel="stylesheet" href="/css/main.css?v=discovery-2"><link rel="icon" type="image/png" href="/favicon.png">
</head>
<body>
<div class="app-container">
    <aside class="sidebar">
        <div class="sidebar-logo"><span class="logo-mark">S</span> Student OS</div>
        <div class="nav-group"><div class="nav-title">Workspace</div><a href="/dashboard" class="nav-link">Overview</a><a href="/goals" class="nav-link">Goals</a><a href="/schedule" class="nav-link">Schedule</a></div>
        <div class="nav-group"><div class="nav-title">Community</div><a href="/skills/discover" class="nav-link active">Discover talent</a><a href="/skills" class="nav-link">My skills</a><a href="/messages" class="nav-link">Messages<c:if test="${unreadMessageCount gt 0}"><span class="notification-badge"><c:out value="${unreadMessageCount}"/></span></c:if></a><a href="/notifications" class="nav-link">Notifications<c:if test="${unreadNotificationCount gt 0}"><span class="notification-badge"><c:out value="${unreadNotificationCount}"/></span></c:if></a><a href="/profile" class="nav-link">My profile</a></div>
        <div class="nav-group"><div class="nav-title">Opportunities</div><a href="/freelance" class="nav-link">Freelance jobs</a><a href="/freelance/services" class="nav-link">Services</a></div>
        <div class="sidebar-footer"><a href="/auth/logout" class="nav-link nav-link-danger">Log out</a></div>
    </aside>
    <main class="main-content">
        <header class="page-header"><div><p class="eyebrow">Student network</p><h1 class="page-title">Discover talent</h1><p class="page-subtitle">Find classmates by skill, learning goal, availability, or the kind of collaboration they want to build.</p></div><a href="/profile" class="btn btn-secondary">Complete my profile</a></header>
        <c:if test="${param.report eq '1'}"><div class="alert alert-success">Thank you. Your report is now in the StudentOS review queue.</div></c:if><c:if test="${param.report eq '0'}"><div class="alert alert-error">That report could not be submitted. You can report each item once while it is open.</div></c:if>
        <section class="profile-form-card discovery-filter-card">
            <div class="profile-section-heading"><div><p class="section-kicker">Find the right fit</p><h2>Search the student network</h2></div><p>Results use only information students choose to share publicly on StudentOS.</p></div>
            <form method="get" action="/skills/discover">
                <div class="discovery-filter-grid">
                    <div class="form-group discovery-query"><label class="form-label" for="q">Search</label><input id="q" name="q" class="form-control" maxlength="100" value="<c:out value='${discoverQuery}'/>" placeholder="Skill, name, major, university, or collaboration interest"></div>
                    <div class="form-group"><label class="form-label" for="type">Goal</label><select id="type" name="type" class="form-control"><option value="">Teach or learn</option><option value="TEACH" <c:if test="${discoverType eq 'TEACH'}">selected</c:if>>Ready to teach</option><option value="LEARN" <c:if test="${discoverType eq 'LEARN'}">selected</c:if>>Looking to learn</option></select></div>
                    <div class="form-group"><label class="form-label" for="level">Skill level</label><select id="level" name="level" class="form-control"><option value="">Any level</option><option value="BEGINNER" <c:if test="${discoverLevel eq 'BEGINNER'}">selected</c:if>>Beginner</option><option value="INTERMEDIATE" <c:if test="${discoverLevel eq 'INTERMEDIATE'}">selected</c:if>>Intermediate</option><option value="ADVANCED" <c:if test="${discoverLevel eq 'ADVANCED'}">selected</c:if>>Advanced</option><option value="EXPERT" <c:if test="${discoverLevel eq 'EXPERT'}">selected</c:if>>Expert</option></select></div>
                    <div class="form-group"><label class="form-label" for="availability">Availability</label><select id="availability" name="availability" class="form-control"><option value="">Any availability</option><option value="OPEN_TO_COLLABORATE" <c:if test="${discoverAvailability eq 'OPEN_TO_COLLABORATE'}">selected</c:if>>Open to collaborate</option><option value="LOOKING_FOR_TEAM" <c:if test="${discoverAvailability eq 'LOOKING_FOR_TEAM'}">selected</c:if>>Looking for a team</option><option value="AVAILABLE_FOR_FREELANCE" <c:if test="${discoverAvailability eq 'AVAILABLE_FOR_FREELANCE'}">selected</c:if>>Available for freelance work</option><option value="FOCUSED_ON_STUDY" <c:if test="${discoverAvailability eq 'FOCUSED_ON_STUDY'}">selected</c:if>>Focused on study</option></select></div>
                    <div class="form-group"><label class="form-label" for="sort">Sort by</label><select id="sort" name="sort" class="form-control"><option value="NEWEST" <c:if test="${discoverSort eq 'NEWEST'}">selected</c:if>>Recently added</option><option value="NAME" <c:if test="${discoverSort eq 'NAME'}">selected</c:if>>Skill name</option><option value="LEVEL" <c:if test="${discoverSort eq 'LEVEL'}">selected</c:if>>Highest level</option></select></div>
                </div>
                <div class="owner-actions"><button class="btn btn-primary" type="submit">Search students</button><a href="/skills/discover" class="btn btn-secondary">Clear filters</a></div>
            </form>
        </section>
        <div class="discovery-results-heading"><div><p class="section-kicker">Search results</p><h2><c:out value="${discoverCount}"/> public skill<c:if test="${discoverCount ne 1}">s</c:if> found</h2></div><p>Page <c:out value="${discoverPage}"/> of <c:out value="${discoverPages}"/></p></div>
        <section class="grid-2">
            <c:choose>
                <c:when test="${empty allSkills}"><div class="card discovery-empty"><h3>No students match this search yet</h3><p>Try a broader word, remove one filter, or explore a different collaboration availability.</p><a href="/skills/discover" class="btn btn-secondary">Reset discovery</a></div></c:when>
                <c:otherwise>
                    <c:forEach var="skill" items="${allSkills}">
                        <article class="card skill-owner-card discovery-card">
                            <div class="card-header"><div><div class="card-title"><c:out value="${skill.skillName}"/></div><p class="owner-meta">Shared by <strong><c:out value="${skill.ownerName}"/></strong></p></div><span class="badge badge-todo"><c:out value="${skill.type}"/></span></div>
                            <div class="discovery-badge-row"><span class="badge badge-completed"><c:out value="${skill.skillLevel}"/></span><c:if test="${not empty skill.availabilityLabel}"><span class="badge badge-todo"><c:out value="${skill.availabilityLabel}"/></span></c:if></div>
                            <p class="discovery-academic"><c:out value="${skill.major}"/><c:if test="${not empty skill.major and not empty skill.university}"> · </c:if><c:out value="${skill.university}"/></p>
                            <c:if test="${not empty skill.collaborationPreferences}"><p class="discovery-preferences"><c:out value="${skill.collaborationPreferences}"/></p></c:if>
                            <div class="owner-actions"><a href="/profile/view?id=<c:out value='${skill.userId}'/>" class="btn btn-secondary">View profile</a><c:if test="${skill.userId ne currentUserId}"><a href="/messages?toEmail=<c:out value='${skill.ownerEmail}'/>" class="btn btn-primary">Message</a><details><summary class="text-action">Report</summary><form action="/reports/new" method="post"><input type="hidden" name="csrfToken" value="<c:out value='${csrfToken}'/>"><input type="hidden" name="targetType" value="SKILL"><input type="hidden" name="targetId" value="<c:out value='${skill.id}'/>"><input type="hidden" name="returnTo" value="/skills/discover"><select name="reason" required><option value="SPAM">Spam</option><option value="HARASSMENT">Harassment</option><option value="INAPPROPRIATE">Inappropriate</option><option value="MISLEADING">Misleading</option><option value="OTHER">Other</option></select><input name="details" maxlength="1000" placeholder="Optional context"><button class="text-action text-action-danger" type="submit">Submit report</button></form></details></c:if></div>
                        </article>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </section>
        <c:if test="${discoverPages gt 1}"><div class="discovery-pagination"><c:if test="${discoverPage gt 1}"><c:url var="previousUrl" value="/skills/discover"><c:param name="q" value="${discoverQuery}"/><c:param name="type" value="${discoverType}"/><c:param name="level" value="${discoverLevel}"/><c:param name="availability" value="${discoverAvailability}"/><c:param name="sort" value="${discoverSort}"/><c:param name="page" value="${discoverPage - 1}"/></c:url><a class="btn btn-secondary" href="${previousUrl}">Previous</a></c:if><span class="page-subtitle">Showing public skills on page <c:out value="${discoverPage}"/></span><c:if test="${discoverPage lt discoverPages}"><c:url var="nextUrl" value="/skills/discover"><c:param name="q" value="${discoverQuery}"/><c:param name="type" value="${discoverType}"/><c:param name="level" value="${discoverLevel}"/><c:param name="availability" value="${discoverAvailability}"/><c:param name="sort" value="${discoverSort}"/><c:param name="page" value="${discoverPage + 1}"/></c:url><a class="btn btn-secondary" href="${nextUrl}">Next</a></c:if></div></c:if>
        <footer class="mkv-footer">© 2026 MKV Team</footer>
    </main>
</div>
<script src="/js/mobile-nav.js?v=mobile-nav-phone-5" defer></script>
</body>
</html>
