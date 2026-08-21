<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Login - Student OS</title>
    <link rel="stylesheet" href="/css/main.css">
    <style>
        .auth-wrapper { display: flex; justify-content: center; align-items: center; min-height: 100vh; background-color: var(--bg-secondary); }
        .auth-card { width: 100%; max-width: 400px; background: var(--bg-primary); padding: 40px; border-radius: 12px; border: 1px solid var(--border-color); box-shadow: var(--shadow-md); }
        .auth-logo { font-size: 24px; font-weight: 700; text-align: center; margin-bottom: 32px; }
        .error-msg { color: var(--danger); font-size: 14px; margin-bottom: 16px; text-align: center; }
        .success-msg { color: var(--success); font-size: 14px; margin-bottom: 16px; text-align: center; }
    </style>
    <link rel="icon" type="image/png" href="/favicon.png">
</head>
<body>
    <div class="auth-wrapper">
        <div class="auth-card">
            <div class="auth-logo"><a href="/">STUDENT OS</a></div>
            <% if (request.getAttribute("error") != null || request.getParameter("error") != null) { %>
                <div class="error-msg">We could not sign you in with those details. Please try again.</div>
            <% } %>
            <% if (request.getParameter("registered") != null) { %>
                <div class="success-msg">Account created! Please log in.</div>
            <% } %>
            <form action="/auth/signin" method="POST">
                <div class="form-group">
                    <label class="form-label">Email</label>
                    <input type="email" name="email" class="form-control" required placeholder="name@university.edu">
                </div>
                <div class="form-group">
                    <label class="form-label">Password</label>
                    <input type="password" name="password" class="form-control" required placeholder="Enter your password" autocomplete="current-password">
                </div>
                <button type="submit" class="btn btn-primary" style="width: 100%; margin-top: 8px;">Sign In</button>
            </form>
            <div style="text-align: center; margin-top: 24px; font-size: 14px; color: var(--text-secondary);">
                Don't have an account? <a href="/auth/register" style="color: var(--text-primary); font-weight: 500;">Register</a>
            </div>
        </div>
    </div>
<footer class="mkv-footer">© 2026 MKV Team</footer>
</body>
</html>
