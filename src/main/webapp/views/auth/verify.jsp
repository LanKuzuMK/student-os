<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Verify Email - Student OS</title>
    <link rel="stylesheet" href="\/css/main.css">
    <style>
        .demo-alert {
            background-color: rgba(99, 102, 241, 0.1);
            border: 1px solid var(--primary);
            color: #fff;
            padding: 1rem;
            border-radius: 8px;
            margin-bottom: 2rem;
            text-align: center;
        }
        .demo-alert strong {
            color: var(--primary);
            font-size: 1.2rem;
            letter-spacing: 2px;
        }
    </style>
    <link rel="icon" type="image/png" href="/favicon.png">
</head>
<body>
    <div class="auth-container">
        <div class="auth-card">
            <h1 class="heading-lg" style="text-align: center; margin-bottom: 1rem;">Verify Your Email</h1>
            <p style="text-align: center; color: var(--text-secondary); margin-bottom: 2rem;">
                We've sent a 6-digit code to <strong>\</strong>.
            </p>
            
            <!-- Mock Email Notification for Demo Purposes -->
            <c:if test="\">
                <div class="demo-alert">
                    <span style="display: block; font-size: 0.8rem; text-transform: uppercase; margin-bottom: 0.5rem; opacity: 0.8;">[Demo Mode - Simulated Email Inbox]</span>
                    Your Student OS Verification Code is:<br>
                    <strong>\</strong>
                </div>
            </c:if>

            <c:if test="\">
                <div style="color: #ef4444; text-align: center; margin-bottom: 1rem;">\</div>
            </c:if>

            <form action="\/auth/verify" method="POST" class="auth-form">
                <input type="text" name="otp" placeholder="Enter 6-digit code" required class="input-field" maxlength="6" style="text-align: center; font-size: 1.5rem; letter-spacing: 5px;">
                <button type="submit" class="btn">Verify & Create Account</button>
            </form>
            
            <p style="text-align: center; margin-top: 1.5rem;">
                <a href="\/views/auth/register.jsp" style="color: var(--text-secondary);">Cancel and go back</a>
            </p>
        </div>
    </div>
</body>
</html>
