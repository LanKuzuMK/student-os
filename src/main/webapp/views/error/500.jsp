<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<% Object trace = request.getAttribute("requestTraceId"); %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Something went wrong - StudentOS</title>
    <link rel="icon" type="image/png" href="/favicon.png">
    <link rel="stylesheet" href="/css/main.css">
</head>
<body>
    <main class="main-content" style="align-items:center;justify-content:center;text-align:center;">
        <div>
            <div class="eyebrow">StudentOS</div>
            <h1 class="page-title">Something went wrong.</h1>
            <p class="page-subtitle" style="margin-inline:auto;">The page could not finish loading. Refresh once, or return to your workspace and try again.</p><% if (trace != null) { %><p style="margin:12px auto 0;color:#7b879b;font-size:12px;">Support reference: <strong><%= trace %></strong></p><% } %>
            <div style="display:flex;justify-content:center;gap:10px;flex-wrap:wrap;margin-top:20px;"><a href="" class="btn btn-primary">Try again</a><a href="/dashboard" class="btn btn-secondary">Go to workspace</a></div>
        </div>
        <footer class="mkv-footer" style="width:100%;">&copy; 2026 MKV Team</footer>
    </main>
</body>
</html>
