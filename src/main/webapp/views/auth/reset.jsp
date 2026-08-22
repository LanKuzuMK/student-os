<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Choose a new password - StudentOS</title>
    <link rel="stylesheet" href="/css/main.css">
    <link rel="icon" type="image/png" href="/favicon.png">
    <style>.auth-wrapper { display:flex; justify-content:center; align-items:center; min-height:100vh; background:var(--bg-secondary); } .auth-card { width:100%; max-width:400px; background:var(--bg-primary); padding:40px; border-radius:12px; border:1px solid var(--border-color); box-shadow:var(--shadow-md); } .auth-logo { font-size:24px; font-weight:700; text-align:center; margin-bottom:18px; } .auth-copy { color:var(--text-secondary); font-size:14px; line-height:1.55; margin-bottom:18px; } .error-msg { color:var(--danger); font-size:14px; margin-bottom:16px; }</style>
</head>
<body>
<div class="auth-wrapper"><div class="auth-card">
    <div class="auth-logo"><a href="/">STUDENT OS</a></div>
    <h1 class="page-title" style="font-size:24px;">Choose a new password</h1>
    <p class="auth-copy">Enter the six-digit code from your email and a new password with at least eight characters, including a letter and a number.</p>
    <c:if test="${param.error eq 'invalid'}"><div class="error-msg">The code or new password could not be accepted. Request a new code and try again.</div></c:if>
    <form action="/auth/reset" method="post">
        <input type="hidden" name="csrfToken" value="<c:out value='${csrfToken}'/>">
        <div class="form-group"><label class="form-label" for="otp">Six-digit code</label><input id="otp" inputmode="numeric" pattern="[0-9]{6}" maxlength="6" name="otp" class="form-control" required autocomplete="one-time-code" placeholder="123456"></div>
        <div class="form-group"><label class="form-label" for="newPassword">New password</label><input id="newPassword" type="password" name="newPassword" class="form-control" required autocomplete="new-password"></div>
        <div class="form-group"><label class="form-label" for="confirmPassword">Confirm new password</label><input id="confirmPassword" type="password" name="confirmPassword" class="form-control" required autocomplete="new-password"></div>
        <button type="submit" class="btn btn-primary" style="width:100%; margin-top:8px;">Save new password</button>
    </form>
    <div style="text-align:center; margin-top:22px; font-size:14px;"><a href="/auth/forgot">Request another code</a></div>
</div></div>
<footer class="mkv-footer">&copy; 2026 MKV Team</footer>
</body>
</html>
