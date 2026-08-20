<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="theme-color" content="#5a5ce2">
    <title>Verify Email - StudentOS</title>
    <link rel="stylesheet" href="/css/main.css?v=verify-auth-2">
    <link rel="icon" type="image/png" href="/favicon.png">
    <style>
        .verify-wrapper { display:flex;align-items:center;justify-content:center;min-height:100vh;padding:24px;background:var(--bg-secondary); }
        .verify-card { width:100%;max-width:430px;padding:36px;background:rgba(255,255,255,.95);border:1px solid var(--border-color);border-radius:20px;box-shadow:var(--shadow-lg);text-align:center; }
        .verify-kicker { color:var(--brand);font-size:10px;font-weight:800;letter-spacing:.12em;text-transform:uppercase; }
        .verify-card h1 { margin:8px 0 10px;color:var(--ink);font-family:'Plus Jakarta Sans',sans-serif;font-size:24px;letter-spacing:-.8px; }
        .verify-card p { color:var(--ink-soft);font-size:14px;line-height:1.55; }
        .verify-code { padding:13px 15px;margin:20px 0;color:#4c4fbd;background:#f2f1ff;border:1px solid #dddffc;border-radius:12px;font-size:13px;line-height:1.45; }
        .verify-code strong { display:block;margin-top:5px;color:#4749bf;font-size:21px;letter-spacing:.24em; }
        .verify-otp { text-align:center;font-size:20px !important;font-weight:800;letter-spacing:.32em; }
        .verify-back { display:block;margin-top:19px;color:var(--ink-soft);font-size:13px;text-decoration:none; }
        .verify-back:hover { color:var(--brand); }
    </style>
</head>
<body>
    <div class="verify-wrapper">
        <section class="verify-card" aria-labelledby="verify-title">
            <div class="auth-logo"><a href="/">STUDENT OS</a></div>
            <div class="verify-kicker">Account security</div>
            <h1 id="verify-title">Verify your email</h1>
            <c:choose>
                <c:when test="${empty sessionScope.pendingEmail}">
                    <p>Start by creating an account. We will then send you to this verification step.</p>
                    <a href="/auth/register" class="btn btn-primary" style="margin-top:18px;">Create an account</a>
                </c:when>
                <c:otherwise>
                    <p>Enter the six-digit code for <strong><c:out value="${sessionScope.pendingEmail}"/></strong>.</p>
                    <c:if test="${not empty sessionScope.otpCode}">
                        <div class="verify-code">Demo verification code<strong><c:out value="${sessionScope.otpCode}"/></strong></div>
                    </c:if>
                    <c:if test="${not empty requestScope.error}"><div class="alert alert-error"><c:out value="${requestScope.error}"/></div></c:if>
                    <form action="/auth/verify" method="POST" style="margin-top:18px;">
                        <div class="form-group"><label class="form-label" for="otp">Verification code</label><input id="otp" type="text" inputmode="numeric" pattern="[0-9]{6}" name="otp" placeholder="123456" required maxlength="6" autocomplete="one-time-code" class="form-control verify-otp"></div>
                        <button type="submit" class="btn btn-primary" style="width:100%;">Verify account</button>
                    </form>
                    <a href="/auth/register" class="verify-back">Cancel and return to registration</a>
                </c:otherwise>
            </c:choose>
        </section>
    </div>
    <footer class="mkv-footer">© 2026 MKV Team</footer>
</body>
</html>
