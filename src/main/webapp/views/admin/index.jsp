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
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Admin — StudentOS</title>
    <link rel="stylesheet" href="/css/main.css?v=admin-1">
    <link rel="icon" type="image/png" href="/favicon.png">
    <style>
        .admin-shell { display:grid; grid-template-columns:220px 1fr; min-height:100vh; }
        .admin-rail  { position:fixed; inset:0 auto 0 0; width:220px; background:#0f172a; color:#e2e8f0; display:flex; flex-direction:column; padding:24px 0; overflow-y:auto; }
        .admin-rail-logo { padding:0 20px 24px; font-family:'Space Grotesk',sans-serif; font-size:17px; font-weight:700; color:#fff; border-bottom:1px solid rgba(255,255,255,.1); }
        .admin-rail-logo span { color:#f87171; font-size:11px; margin-left:6px; font-weight:600; letter-spacing:.06em; }
        .admin-nav { padding:16px 0; flex:1; }
        .admin-nav a { display:block; padding:10px 20px; color:#94a3b8; font-size:13px; font-weight:600; text-decoration:none; transition:background .15s,color .15s; }
        .admin-nav a:hover, .admin-nav a.active { background:rgba(255,255,255,.08); color:#fff; }
        .admin-rail-footer { padding:16px 20px; border-top:1px solid rgba(255,255,255,.1); }
        .admin-rail-footer a { color:#f87171; font-size:12px; text-decoration:none; }
        .admin-main { margin-left:220px; padding:40px clamp(20px,4vw,56px); }
        .admin-header { margin-bottom:32px; }
        .admin-header h1 { margin:0; font-family:'Space Grotesk',sans-serif; font-size:28px; font-weight:700; color:#0f172a; }
        .admin-header p  { margin:6px 0 0; color:#64748b; font-size:14px; }
        .stat-grid { display:grid; grid-template-columns:repeat(auto-fill,minmax(180px,1fr)); gap:16px; margin-bottom:40px; }
        .stat-card { background:#fff; border:1px solid #e2e8f0; border-radius:14px; padding:22px 20px; }
        .stat-card .val { font-family:'Space Grotesk',sans-serif; font-size:34px; font-weight:700; color:#0f172a; line-height:1; }
        .stat-card .lbl { margin-top:8px; color:#64748b; font-size:11px; font-weight:600; letter-spacing:.07em; text-transform:uppercase; }
        .stat-card.danger .val { color:#dc2626; }
        .admin-section-title { font-family:'Space Grotesk',sans-serif; font-size:16px; font-weight:700; color:#0f172a; margin:0 0 14px; }
        .quick-links { display:flex; gap:12px; flex-wrap:wrap; }
        .quick-link { display:inline-flex; align-items:center; gap:8px; padding:10px 18px; border-radius:10px; background:#f1f5f9; color:#0f172a; font-size:13px; font-weight:600; text-decoration:none; transition:background .15s; }
        .quick-link:hover { background:#e2e8f0; }
    </style>
</head>
<body>
<div class="admin-shell">
    <aside class="admin-rail">
        <div class="admin-rail-logo">StudentOS <span>ADMIN</span></div>
        <nav class="admin-nav">
            <a href="/admin" class="active">Dashboard</a>
            <a href="/admin/users">Users</a>
            <a href="/admin/content">Content</a>
            <a href="/admin/messages">Messages</a>
            <a href="/dashboard">Back to App</a>
        </nav>
        <div class="admin-rail-footer"><a href="/auth/logout">Logout</a></div>
    </aside>
    <main class="admin-main">
        <div class="admin-header">
            <h1>Platform Dashboard</h1>
            <p>Live stats from your Neon database.</p>
        </div>
        <div class="stat-grid">
            <div class="stat-card"><div class="val"><%=totalUsers%></div><div class="lbl">Total Users</div></div>
            <div class="stat-card danger"><div class="val"><%=bannedUsers%></div><div class="lbl">Banned Users</div></div>
            <div class="stat-card"><div class="val"><%=totalSkills%></div><div class="lbl">Skills Listed</div></div>
            <div class="stat-card"><div class="val"><%=totalJobs%></div><div class="lbl">Freelance Jobs</div></div>
            <div class="stat-card"><div class="val"><%=totalServices%></div><div class="lbl">Services</div></div>
            <div class="stat-card"><div class="val"><%=totalMessages%></div><div class="lbl">Messages Sent</div></div>
            <div class="stat-card"><div class="val"><%=totalGoals%></div><div class="lbl">Goals</div></div>
            <div class="stat-card"><div class="val"><%=totalTasks%></div><div class="lbl">Tasks</div></div>
        </div>
        <p class="admin-section-title">Quick Actions</p>
        <div class="quick-links">
            <a class="quick-link" href="/admin/users">Manage Users</a>
            <a class="quick-link" href="/admin/content">Moderate Content</a>
            <a class="quick-link" href="/admin/messages">View Messages</a>
        </div>
        <footer class="mkv-footer" style="margin-top:60px;">© 2026 MKV Team</footer>
    </main>
</div>
</body>
</html>
