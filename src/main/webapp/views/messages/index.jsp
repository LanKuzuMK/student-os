<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="theme-color" content="#5a5ce2">
    <title>Messages - StudentOS</title>
    <link rel="stylesheet" href="/css/main.css?v=messages-email-2">
    <link rel="icon" type="image/png" href="/favicon.png">
</head>
<body>
<div class="app-container">
    <aside class="sidebar">
        <div class="sidebar-logo"><span class="logo-mark">S</span> Student OS</div>
        <div class="nav-group"><div class="nav-title">Workspace</div><a href="/dashboard" class="nav-link">Overview</a><a href="/goals" class="nav-link">Goals</a><a href="/schedule" class="nav-link">Schedule</a></div>
        <div class="nav-group"><div class="nav-title">Community</div><a href="/skills/discover" class="nav-link">Discover talent</a><a href="/skills" class="nav-link">My skills</a><a href="/messages" class="nav-link active">Messages</a></div>
        <div class="nav-group"><div class="nav-title">Opportunities</div><a href="/freelance" class="nav-link">Freelance jobs</a><a href="/freelance/services" class="nav-link">Services</a></div>
        <div class="sidebar-footer"><a href="/auth/logout" class="nav-link nav-link-danger">Log out</a></div>
    </aside>
    <main class="main-content">
        <header class="page-header"><div><p class="eyebrow">Community inbox</p><h1 class="page-title">Messages</h1><p class="page-subtitle">Reach classmates by email, collaborate on skills, and manage your own conversation history.</p></div></header>
        <c:if test="${param.sent eq '1'}"><div class="alert alert-success">Your message was sent.</div></c:if>
        <c:if test="${param.error eq '1'}"><div class="alert alert-error">Enter a valid classmate email and a message before sending.</div></c:if>
        <c:if test="${param.cleared eq '1'}"><div class="alert alert-success">That conversation was removed from your history.</div></c:if>
        <c:if test="${param.clearError eq '1'}"><div class="alert alert-error">We could not remove that conversation. Try again.</div></c:if>

        <div class="messages-layout">
            <section class="compose-card">
                <div class="section-kicker">New message</div><h2>Reach out with context</h2><p>Use a classmate’s StudentOS email to start a conversation. Discover links will prefill this field for you.</p>
                <form action="/messages" method="post">
                    <input type="hidden" name="action" value="send">
                    <div class="form-group"><label class="form-label" for="recipientEmail">Classmate email</label><input id="recipientEmail" class="form-control" type="email" name="recipientEmail" value="<c:out value='${recipientEmail}'/>" placeholder="classmate@example.com" required autocomplete="email"></div>
                    <div class="form-group"><label class="form-label" for="content">Message</label><textarea id="content" class="form-control" name="content" rows="6" placeholder="Hi! I saw your skill profile and would love to connect about…" required></textarea></div>
                    <button class="btn btn-primary" type="submit">Send message</button>
                </form>
            </section>
            <section class="inbox-card">
                <div class="section-kicker">Conversation history</div><h2>Your recent messages</h2>
                <div class="message-list">
                    <c:choose>
                        <c:when test="${empty messages}"><div class="empty-state"><h3>No messages yet</h3><p>Use Discover to find students with complementary skills and start the first conversation.</p><a href="/skills/discover" class="btn btn-secondary">Discover students</a></div></c:when>
                        <c:otherwise>
                            <c:set var="previousCounterpart" value="" />
                            <c:forEach var="msg" items="${messages}">
                                <article class="message-item">
                                    <div class="message-avatar">S</div>
                                    <div class="message-content"><div class="message-meta"><strong><c:out value="${msg.counterpartEmail}"/></strong><span>Student conversation</span></div><p><c:out value="${msg.content}"/></p></div>
                                    <c:if test="${msg.counterpartEmail ne previousCounterpart}"><form class="message-clear-form" action="/messages" method="post" onsubmit="return confirm('Remove this entire conversation from your history? The other student will keep their copy.');"><input type="hidden" name="action" value="clear"><input type="hidden" name="counterpartEmail" value="<c:out value='${msg.counterpartEmail}'/>"><button class="message-clear" type="submit">Delete history</button></form></c:if>
                                </article>
                                <c:set var="previousCounterpart" value="${msg.counterpartEmail}" />
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </div>
            </section>
        </div>
        <footer class="mkv-footer">© 2026 MKV Team</footer>
    </main>
</div>
</body>
</html>
