<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Messages - Student OS</title>
    <link rel="stylesheet" href="\/css/main.css">
</head>
<body>
    <nav class="navbar">
        <div class="logo">Student OS</div>
        <div class="nav-links">
            <a href="\/dashboard">Dashboard</a>
            <a href="\/messages">Messages</a>
        </div>
    </nav>
    <div class="container" style="margin-top: 4rem;">
        <h1 class="heading-xl">Messages</h1>
        <form action="\/messages" method="POST" class="auth-form" style="max-width: 600px; margin-bottom: 2rem;">
            <input type="number" name="receiverId" placeholder="Receiver User ID" required class="input-field">
            <textarea name="content" placeholder="Type a message..." required class="input-field"></textarea>
            <button type="submit" class="btn">Send Message</button>
        </form>
        <div class="dashboard-grid" style="display: block;">
            <c:forEach var="msg" items="\">
                <div class="card" style="margin-bottom: 1rem;">
                    <strong>From: User \ | To: User \</strong>
                    <p>\</p>
                </div>
            </c:forEach>
        </div>
    </div>
</body>
</html>
