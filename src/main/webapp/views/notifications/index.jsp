<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Notifications - StudentOS</title>
    <link rel="stylesheet" href="/css/main.css?v=notifications-1">
    <link rel="icon" type="image/png" href="/favicon.png">
</head>
<body>
<div class="app-container">
    <aside class="sidebar">
        <div class="sidebar-logo"><span class="logo-mark">S</span> Student OS</div>
        <div class="nav-group"><div class="nav-title">Workspace</div><a href="/dashboard" class="nav-link">Overview</a><a href="/goals" class="nav-link">Goals</a><a href="/schedule" class="nav-link">Schedule</a></div>
        <div class="nav-group"><div class="nav-title">Community</div><a href="/skills/discover" class="nav-link">Discover talent</a><a href="/skills" class="nav-link">My skills</a><a href="/messages" class="nav-link">Messages<c:if test="${unreadMessageCount gt 0}"><span class="notification-badge"><c:out value="${unreadMessageCount}"/></span></c:if></a><a href="/notifications" class="nav-link active">Notifications<c:if test="${unreadNotificationCount gt 0}"><span class="notification-badge"><c:out value="${unreadNotificationCount}"/></span></c:if></a><a href="/profile" class="nav-link">My profile</a></div>
        <div class="nav-group"><div class="nav-title">Opportunities</div><a href="/freelance" class="nav-link">Freelance jobs</a><a href="/freelance/services" class="nav-link">Services</a></div>
        <div class="sidebar-footer"><a href="/auth/logout" class="nav-link nav-link-danger">Log out</a></div>
    </aside>
    <main class="main-content">
        <header class="page-header">
            <div><p class="eyebrow">Student activity</p><h1 class="page-title">Notifications</h1><p class="page-subtitle">Updates about messages, account actions, and moderation are kept here.</p></div>
            <c:if test="${not empty notifications}"><form method="post" action="/notifications"><input type="hidden" name="csrfToken" value="<c:out value='${csrfToken}'/>"><input type="hidden" name="action" value="all"><button class="btn btn-secondary" type="submit">Mark all as read</button></form></c:if>
        </header>
        <section class="profile-form-card">
            <c:choose>
                <c:when test="${not empty notifications}">
                    <div class="custom-link-list">
                        <c:forEach items="${notifications}" var="notification">
                            <article class="custom-profile-link-row">
                                <div><p class="section-kicker"><c:out value="${notification.type}"/> <c:if test="${not notification.read}">· New</c:if></p><strong><c:out value="${notification.title}"/></strong><span><c:out value="${notification.message}"/></span><small><c:out value="${notification.createdAt}"/></small></div>
                                <div class="page-header-actions"><c:if test="${not empty notification.actionUrl}"><a class="btn btn-secondary" href="<c:out value='${notification.actionUrl}'/>">Open</a></c:if><c:if test="${not notification.read}"><form method="post" action="/notifications"><input type="hidden" name="csrfToken" value="<c:out value='${csrfToken}'/>"><input type="hidden" name="notificationId" value="<c:out value='${notification.id}'/>"><button class="text-action" type="submit">Mark read</button></form></c:if></div>
                            </article>
                        </c:forEach>
                    </div>
                </c:when>
                <c:otherwise><p class="profile-empty-copy">You are all caught up. New messages, account updates, and moderation outcomes will appear here.</p></c:otherwise>
            </c:choose>
        </section>
        <footer class="mkv-footer">© 2026 MKV Team</footer>
    </main>
</div>
<script src="/js/mobile-nav.js?v=mobile-nav-phone-5" defer></script>
</body>
</html>
