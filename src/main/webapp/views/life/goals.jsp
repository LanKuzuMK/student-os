<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Goals - Life OS</title>
    <link rel="stylesheet" href="\/css/main.css">
</head>
<body>
    <nav class="navbar">
        <div class="logo">Student OS</div>
        <div class="nav-links">
            <a href="\/dashboard">Dashboard</a>
            <a href="\/goals">Goals</a>
        </div>
    </nav>
    <div class="container" style="margin-top: 4rem;">
        <h1 class="heading-xl">My Goals</h1>
        <form action="\/goals" method="POST" class="auth-form" style="max-width: 600px; margin-bottom: 2rem;">
            <input type="text" name="title" placeholder="Goal Title" required class="input-field">
            <textarea name="description" placeholder="Goal Description" required class="input-field"></textarea>
            <input type="number" name="progress" placeholder="Progress %" min="0" max="100" required class="input-field">
            <button type="submit" class="btn">Create Goal</button>
        </form>
        <div class="skills-grid">
            <c:forEach var="goal" items="\">
                <div class="skill-card">
                    <h3>\</h3>
                    <p style="color: var(--text-secondary);">\</p>
                    <div style="margin-top: 1rem; background: #333; height: 10px; border-radius: 5px;">
                        <div style="width: \%; background: var(--primary); height: 100%; border-radius: 5px;"></div>
                    </div>
                    <small>\% Complete</small>
                </div>
            </c:forEach>
        </div>
    </div>
</body>
</html>
