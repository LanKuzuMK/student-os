<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Change password - StudentOS</title>
    <link rel="stylesheet" href="/css/main.css">
    <link rel="icon" type="image/png" href="/favicon.png">
    <style>.auth-wrapper { display:flex; justify-content:center; align-items:center; min-height:100vh; background:var(--bg-secondary); } .auth-card { width:100%; max-width:420px; background:var(--bg-primary); padding:40px; border-radius:12px; border:1px solid var(--border-color); box-shadow:var(--shadow-md); } .auth-logo { font-size:24px; font-weight:700; text-align:center; margin-bottom:18px; } .auth-copy { color:var(--text-secondary); font-size:14px; line-height:1.55; margin-bottom:18px; } .error-msg { color:var(--danger); font-size:14px; margin-bottom:16px; } .success-msg { color:var(--success); font-size:14px; margin-bottom:16px; }</style>
</head>
<body>
<div class="auth-wrapper"><div class="auth-card">
    <div class="auth-logo"><a href="/dashboard">STUDENT OS</a></div>
    <h1 class="page-title" style="font-size:24px;">Change password</h1>
    <p class="auth-copy">Confirm your current password, then choose a new password with at least eight characters, including a letter and a number.</p>
    <c:if test="${param.success eq '1'}"><div class="success-msg">Your password was changed. Your current session was refreshed.</div></c:if>
    <c:if test="${param.error eq 'mismatch'}"><div class="error-msg">Your new password and confirmation do not match.</div></c:if>
    <c:if test="${param.error eq 'invalid'}"><div class="error-msg">We could not change the password. Check your current password and the new password requirements.</div></c:if>
    <form action="/account/password" method="post">
        <input type="hidden" name="csrfToken" value="<c:out value='${csrfToken}'/>">
        <div class="form-group"><label class="form-label" for="currentPassword">Current password</label><input id="currentPassword" type="password" name="currentPassword" class="form-control" required autocomplete="current-password"></div>
        <div class="form-group"><label class="form-label" for="newPassword">New password</label><input id="newPassword" type="password" name="newPassword" class="form-control" required autocomplete="new-password"></div>
        <div class="form-group"><label class="form-label" for="confirmPassword">Confirm new password</label><input id="confirmPassword" type="password" name="confirmPassword" class="form-control" required autocomplete="new-password"></div>
        <button type="submit" class="btn btn-primary" style="width:100%; margin-top:8px;">Change password</button>
    </form>
    <div style="text-align:center; margin-top:22px; font-size:14px;"><a href="/profile">Back to my profile</a></div>
</div></div>
<footer class="mkv-footer">&copy; 2026 MKV Team</footer>
</body>
</html>
