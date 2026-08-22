<%-- Design direction: calm editorial control room with clear data hierarchy and deliberate actions. --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.Map" %>
<%
    Map<String,Integer> stats = (Map<String,Integer>) request.getAttribute("stats");
    if (stats == null) stats = new java.util.LinkedHashMap<>();
    int totalUsers    = stats.getOrDefault("totalUsers",    0);
    int bannedUsers   = stats.getOrDefault("bannedUsers",   0);
    int totalSkills   = stats.getOrDefault("totalSkills",   0);
    int totalJobs     = stats.getOrDefault("totalJobs",     0);
    int totalServices = stats.getOrDefault("totalServices", 0);
    int totalMessages = stats.getOrDefault("totalMessages", 0);
    int totalGoals    = stats.getOrDefault("totalGoals",    0);
    int totalTasks    = stats.getOrDefault("totalTasks",    0);
    int openReports   = stats.getOrDefault("openReports",   0);
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin — StudentOS</title>
    <link rel="stylesheet" href="/css/main.css?v=admin-ui-20260822">
    <link rel="icon" type="image/png" href="/favicon.png">
</head>
<body>
<div class="admin-shell">
    <aside class="admin-rail">
        <div class="admin-rail-logo">StudentOS <span>Admin</span></div>
        <nav class="admin-nav" aria-label="Administrator navigation">
            <a href="/admin" class="active">Dashboard</a>
            <a href="/admin/users">Users</a>
            <a href="/admin/content">Content</a>
            <a href="/admin/messages">Messages</a>
            <a href="/admin/reports">Reports</a>
            <a href="/admin/audit">Audit log</a>
            <a href="/dashboard">Back to App</a>
        </nav>
        <div class="admin-rail-footer"><a href="/auth/logout">Logout</a></div>
    </aside>
    <main class="admin-main">
        <header class="admin-header">
            <div>
                <p class="eyebrow">Platform overview</p>
                <h1>StudentOS control room</h1>
                <p>Monitor members, content, activity, and safety work from one focused administration workspace.</p>
            </div>
            <span class="admin-status">Live database connection</span>
        </header>

        <section class="admin-command-hero" aria-labelledby="admin-command-title">
            <div class="admin-command-copy">
                <p class="admin-command-kicker">Operational snapshot</p>
                <h2 id="admin-command-title">Keep your student community clear, safe, and moving forward.</h2>
                <p>Review reports, manage accounts, and follow platform activity without losing the wider picture.</p>
                <div class="admin-command-actions">
                    <a class="btn" href="/admin/reports">Review reports</a>
                    <a class="btn btn-secondary" href="/admin/users">Manage users</a>
                </div>
            </div>
            <aside class="admin-command-aside">
                <span>Open review work</span>
                <strong><%=openReports%></strong>
                <p><%=openReports == 1 ? "Report awaits review" : "Reports await review"%></p>
            </aside>
        </section>

        <section class="stat-grid" aria-label="Platform statistics">
            <article class="stat-card"><div class="val"><%=totalUsers%></div><div class="lbl">Total members</div></article>
            <article class="stat-card danger"><div class="val"><%=bannedUsers%></div><div class="lbl">Restricted accounts</div></article>
            <article class="stat-card"><div class="val"><%=totalSkills%></div><div class="lbl">Skills listed</div></article>
            <article class="stat-card"><div class="val"><%=totalJobs%></div><div class="lbl">Freelance jobs</div></article>
            <article class="stat-card"><div class="val"><%=totalServices%></div><div class="lbl">Services offered</div></article>
            <article class="stat-card"><div class="val"><%=totalMessages%></div><div class="lbl">Messages sent</div></article>
            <article class="stat-card"><div class="val"><%=totalGoals%></div><div class="lbl">Goals tracked</div></article>
            <article class="stat-card"><div class="val"><%=totalTasks%></div><div class="lbl">Tasks created</div></article>
        </section>

        <section class="admin-workbench" aria-label="Administrator quick actions">
            <article class="admin-workbench-card">
                <p class="eyebrow">Safety queue</p>
                <h2>Moderation reports</h2>
                <p><%=openReports == 0 ? "No open reports right now. Keep an eye on future submissions." : "Review active cases and leave clear resolution notes for the audit history."%></p>
                <a class="btn btn-secondary" href="/admin/reports">Open queue</a>
            </article>
            <article class="admin-workbench-card">
                <p class="eyebrow">Community</p>
                <h2>Member administration</h2>
                <p>Review roles, account status, and recovery actions while preserving server-enforced permissions.</p>
                <a class="btn btn-secondary" href="/admin/users">Open members</a>
            </article>
            <article class="admin-workbench-card">
                <p class="eyebrow">Traceability</p>
                <h2>Audit history</h2>
                <p>See recorded moderation and administration actions with the relevant target and review reason.</p>
                <a class="btn btn-secondary" href="/admin/audit">Open audit log</a>
            </article>
        </section>
        <footer class="mkv-footer">© 2026 MKV Team</footer>
    </main>
</div>
<script src="/js/admin-nav.js?v=20260822"></script>
</body>
</html>
