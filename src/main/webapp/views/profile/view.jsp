<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${profile.displayName}"/> - StudentOS</title>
    <link rel="stylesheet" href="/css/main.css?v=mobile-nav-phone-5">
    <link rel="icon" type="image/png" href="/favicon.png">
</head>
<body>
<div class="app-container">
    <aside class="sidebar">
        <div class="sidebar-logo"><span class="logo-mark">S</span> Student OS</div>
        <div class="nav-group"><div class="nav-title">Workspace</div><a href="/dashboard" class="nav-link">Overview</a><a href="/goals" class="nav-link">Goals</a><a href="/schedule" class="nav-link">Schedule</a></div>
        <div class="nav-group"><div class="nav-title">Community</div><a href="/skills/discover" class="nav-link">Discover talent</a><a href="/skills" class="nav-link">My skills</a><a href="/messages" class="nav-link">Messages<c:if test="${unreadMessageCount gt 0}"><span class="notification-badge"><c:out value="${unreadMessageCount}"/></span></c:if></a><a href="/notifications" class="nav-link">Notifications<c:if test="${unreadNotificationCount gt 0}"><span class="notification-badge"><c:out value="${unreadNotificationCount}"/></span></c:if></a><a href="/profile" class="nav-link">My profile</a></div>
        <div class="nav-group"><div class="nav-title">Opportunities</div><a href="/freelance" class="nav-link">Freelance jobs</a><a href="/freelance/services" class="nav-link">Services</a></div>
        <div class="sidebar-footer"><a href="/auth/logout" class="nav-link nav-link-danger">Log out</a></div>
    </aside>
    <main class="main-content">
        <a href="javascript:history.back()" class="back-link">Back to previous page</a>
        <section class="public-profile-hero">
            <div class="public-profile-avatar"><c:choose><c:when test="${profile.hasAvatar}"><img src="/profile/avatar?id=<c:out value='${profile.userId}'/>" alt="<c:out value='${profile.displayName}'/> profile photo" class="profile-photo-xl"></c:when><c:otherwise><div class="profile-photo-placeholder profile-photo-xl">S</div></c:otherwise></c:choose></div>
            <div class="public-profile-intro"><p class="eyebrow">StudentOS member</p><h1><c:out value="${profile.displayName}"/></h1><c:if test="${not empty profile.major}"><p class="profile-major"><c:out value="${profile.major}"/><c:if test="${not empty profile.university}"> · <c:out value="${profile.university}"/></c:if></p></c:if><p class="profile-email"><c:out value="${profile.email}"/></p></div>
            <div class="public-profile-actions"><c:choose><c:when test="${isOwnProfile}"><a class="btn btn-primary" href="/profile">Edit profile</a></c:when><c:otherwise><a class="btn btn-primary" href="/collaborations?to=<c:out value='${profile.userId}'/>">Propose collaboration</a><a class="btn btn-secondary" href="/messages?toEmail=<c:out value='${profile.email}'/>">Message student</a></c:otherwise></c:choose></div>
        </section>

        <section class="public-profile-grid">
            <article class="profile-detail-card"><p class="section-kicker">About</p><h2>Student summary</h2><c:choose><c:when test="${not empty profile.bio}"><p class="profile-bio"><c:out value="${profile.bio}"/></p></c:when><c:otherwise><p class="profile-empty-copy">This student has not added a description yet.</p></c:otherwise></c:choose></article>
            <article class="profile-detail-card"><p class="section-kicker">Connect</p><h2>Public links</h2><div class="profile-link-list"><c:choose><c:when test="${not empty profile.portfolioUrl}"><a href="<c:out value='${profile.portfolioUrl}'/>" target="_blank" rel="noopener noreferrer" class="profile-link">Portfolio or website</a></c:when></c:choose><c:choose><c:when test="${not empty profile.linkedinUrl}"><a href="<c:out value='${profile.linkedinUrl}'/>" target="_blank" rel="noopener noreferrer" class="profile-link">LinkedIn</a></c:when></c:choose><c:choose><c:when test="${not empty profile.telegramUrl}"><a href="<c:out value='${profile.telegramUrl}'/>" target="_blank" rel="noopener noreferrer" class="profile-link">Telegram</a></c:when></c:choose><c:forEach items="${profileLinks}" var="customLink"><a href="<c:out value='${customLink.url}'/>" target="_blank" rel="noopener noreferrer" class="profile-link"><c:out value="${customLink.label}"/></a></c:forEach><c:if test="${empty profile.portfolioUrl and empty profile.linkedinUrl and empty profile.telegramUrl and empty profileLinks}"><p class="profile-empty-copy">No public links have been added yet.</p></c:if></div></article>
            <article class="profile-detail-card"><p class="section-kicker">Collaboration</p><h2>How this student is available</h2><c:choose><c:when test="${not empty profile.availabilityLabel}"><p class="profile-bio"><strong><c:out value="${profile.availabilityLabel}"/></strong></p><c:if test="${not empty profile.collaborationPreferences}"><p class="profile-bio"><c:out value="${profile.collaborationPreferences}"/></p></c:if></c:when><c:otherwise><p class="profile-empty-copy">This student has not added collaboration preferences yet.</p></c:otherwise></c:choose></article>
        </section>
        <section class="profile-form-card"><div class="profile-section-heading"><div><p class="section-kicker">Project cards</p><h2>Selected work</h2></div><p>External examples shared by this student.</p></div><div class="custom-link-list"><c:choose><c:when test="${not empty profileProjects}"><c:forEach items="${profileProjects}" var="project"><article class="custom-profile-link-row"><a href="<c:out value='${project.url}'/>" target="_blank" rel="noopener noreferrer"><strong><c:out value="${project.title}"/></strong><span><c:out value="${project.description}"/></span></a><a class="btn btn-secondary" href="<c:out value='${project.url}'/>" target="_blank" rel="noopener noreferrer">Open project</a></article></c:forEach></c:when><c:otherwise><p class="profile-empty-copy">No public project cards have been added yet.</p></c:otherwise></c:choose></div></section>
        <footer class="mkv-footer">© 2026 MKV Team</footer>
    </main>
</div>
    <script src="/js/mobile-nav.js?v=mobile-nav-phone-5" defer></script>
</body>
</html>
