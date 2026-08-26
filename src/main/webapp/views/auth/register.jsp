<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="theme-color" content="#5a5ce2">
    <title>Register - StudentOS</title>
    <link rel="stylesheet" href="/css/main.css?v=register-auth-2">
    <link rel="icon" type="image/png" href="/favicon.png">
    <style>
        .auth-wrapper { display: flex; justify-content: center; align-items: center; min-height: 100vh; background-color: var(--bg-secondary); }
        .auth-card { width: 100%; max-width: 400px; background: var(--bg-primary); padding: 40px; border-radius: 12px; border: 1px solid var(--border-color); box-shadow: var(--shadow-md); }
        .auth-logo { font-size: 24px; font-weight: 700; text-align: center; margin-bottom: 32px; }
        .error-msg { color: var(--danger); font-size: 14px; margin-bottom: 16px; text-align: center; }
    </style>
</head>
<body>
    <div class="auth-wrapper">
        <div class="auth-card">
            <div class="auth-logo"><a href="/">STUDENT OS</a></div>
            <div style="text-align:center;color:var(--text-secondary);margin-bottom:24px;font-size:14px;">Create your account</div>
            <% if (request.getAttribute("error") != null || request.getParameter("error") != null) { %>
                <div class="error-msg">We could not send a verification code. Check your details and try again shortly.</div>
            <% } %>
            <form action="/auth/register" method="POST">
                <input type="hidden" name="csrfToken" value="<c:out value='${csrfToken}'/>">
                <div class="form-group">
                    <label class="form-label" for="registerEmail">Email</label>
                    <input id="registerEmail" type="email" name="email" class="form-control" required autocomplete="email" placeholder="name@university.edu">
                </div>
                <div class="form-group">
                    <label class="form-label" for="registerPassword">Password</label>
                    <input id="registerPassword" type="password" name="password" class="form-control" required minlength="8" autocomplete="new-password" placeholder="Choose at least 8 characters">
                </div>
                <p style="margin:0 0 16px;color:var(--text-secondary);font-size:12px;line-height:1.5;">We will send a six-digit verification code to this email before creating your account.</p>
                <button type="submit" class="btn btn-primary" style="width:100%;margin-top:8px;">Create account</button>
            </form>
            <div style="text-align:center;margin-top:24px;font-size:14px;color:var(--text-secondary);">
                Already have an account? <a href="/auth/signin" style="color:var(--text-primary);font-weight:500;">Log in</a>
            </div>
        </div>
    </div>
    <footer class="mkv-footer">© 2026 MKV Team</footer>
</body>
</html>
