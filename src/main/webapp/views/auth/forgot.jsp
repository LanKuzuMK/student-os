<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reset password - StudentOS</title>
    <link rel="stylesheet" href="/css/main.css">
    <link rel="icon" type="image/png" href="/favicon.png">
    <style>.auth-wrapper { display:flex; justify-content:center; align-items:center; min-height:100vh; background:var(--bg-secondary); } .auth-card { width:100%; max-width:400px; background:var(--bg-primary); padding:40px; border-radius:12px; border:1px solid var(--border-color); box-shadow:var(--shadow-md); } .auth-logo { font-size:24px; font-weight:700; text-align:center; margin-bottom:18px; } .auth-copy { color:var(--text-secondary); font-size:14px; line-height:1.55; margin-bottom:18px; } .success-msg { color:var(--success); font-size:14px; margin-bottom:16px; }</style>
</head>
<body>
<div class="auth-wrapper"><div class="auth-card">
    <div class="auth-logo"><a href="/">STUDENT OS</a></div>
    <h1 class="page-title" style="font-size:24px;">Reset your password</h1>
    <p class="auth-copy">Enter your account email. If an active account matches it, StudentOS will send a six-digit reset code.</p>
    <c:if test="${param.sent eq '1'}"><div class="success-msg">If an active StudentOS account uses that email, a reset code has been sent. Check your inbox, then enter it below.</div></c:if>
    <form action="/auth/forgot" method="post">
        <input type="hidden" name="csrfToken" value="<c:out value='${csrfToken}'/>">
        <div class="form-group"><label class="form-label" for="email">Email</label><input id="email" type="email" name="email" class="form-control" required autocomplete="email" placeholder="name@university.edu"></div>
        <button type="submit" class="btn btn-primary" style="width:100%; margin-top:8px;">Email reset code</button>
    </form>
    <div style="text-align:center; margin-top:22px; font-size:14px;"><a href="/auth/signin">Back to sign in</a> · <a href="/auth/reset">Enter a reset code</a></div>
</div></div>
<footer class="mkv-footer">© 2026 MKV Team</footer>
</body>
</html>
