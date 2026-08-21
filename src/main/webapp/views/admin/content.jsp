<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>
<%
    List<Map<String,Object>> skills   = (List<Map<String,Object>>) request.getAttribute("skills");
    List<Map<String,Object>> jobs     = (List<Map<String,Object>>) request.getAttribute("jobs");
    List<Map<String,Object>> services = (List<Map<String,Object>>) request.getAttribute("services");
    if (skills   == null) skills   = new ArrayList<>();
    if (jobs     == null) jobs     = new ArrayList<>();
    if (services == null) services = new ArrayList<>();
    String msg = request.getParameter("msg");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Content — Admin — StudentOS</title>
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
        .admin-header { margin-bottom:28px; }
        .admin-header h1 { margin:0; font-family:'Space Grotesk',sans-serif; font-size:26px; font-weight:700; color:#0f172a; }
        .toast { display:inline-block; margin-bottom:18px; padding:10px 16px; border-radius:8px; background:#dcfce7; color:#166534; font-size:13px; font-weight:600; }
        .section-title { font-family:'Space Grotesk',sans-serif; font-size:16px; font-weight:700; color:#0f172a; margin:32px 0 12px; }
        .admin-table { width:100%; border-collapse:collapse; background:#fff; border:1px solid #e2e8f0; border-radius:12px; overflow:hidden; font-size:13px; }
        .admin-table th { background:#f8fafc; padding:12px 14px; text-align:left; color:#64748b; font-size:11px; font-weight:700; letter-spacing:.07em; text-transform:uppercase; border-bottom:1px solid #e2e8f0; }
        .admin-table td { padding:12px 14px; border-bottom:1px solid #f1f5f9; vertical-align:middle; max-width:320px; overflow:hidden; text-overflow:ellipsis; white-space:nowrap; }
        .admin-table tr:last-child td { border-bottom:0; }
        .btn-sm { padding:5px 10px; border:0; border-radius:7px; font-size:11px; font-weight:700; cursor:pointer; background:#fee2e2; color:#dc2626; }
    </style>
</head>
<body>
<div class="admin-shell">
    <aside class="admin-rail">
        <div class="admin-rail-logo">StudentOS <span>ADMIN</span></div>
        <nav class="admin-nav">
            <a href="/admin">Dashboard</a>
            <a href="/admin/users">Users</a>
            <a href="/admin/content" class="active">Content</a>
            <a href="/admin/messages">Messages</a>
            <a href="/dashboard">Back to App</a>
        </nav>
        <div class="admin-rail-footer"><a href="/auth/logout">Logout</a></div>
    </aside>
    <main class="admin-main">
        <div class="admin-header"><h1>Content Moderation</h1></div>
        <% if (msg != null) { %>
        <div class="toast">Removed: <%=msg.replace("_"," ")%></div>
        <% } %>

        <p class="section-title">Skills (<%=skills.size()%>)</p>
        <table class="admin-table">
            <thead><tr><th>ID</th><th>Owner</th><th>Skill</th><th>Description</th><th>Action</th></tr></thead>
            <tbody>
            <% for (Map<String,Object> r : skills) { %>
            <tr>
                <td><%=r.get("id")%></td>
                <td><%=r.get("email")%></td>
                <td><%=r.get("skill_name")%></td>
                <td><%=r.get("description")%></td>
                <td><form method="post" action="/admin/content/delete-skill" style="margin:0">
                    <input type="hidden" name="id" value="<%=r.get("id")%>">
                    <button class="btn-sm" onclick="return confirm('Delete this skill?')">Delete</button>
                </form></td>
            </tr>
            <% } %>
            </tbody>
        </table>

        <p class="section-title">Freelance Jobs (<%=jobs.size()%>)</p>
        <table class="admin-table">
            <thead><tr><th>ID</th><th>Owner</th><th>Title</th><th>Status</th><th>Action</th></tr></thead>
            <tbody>
            <% for (Map<String,Object> r : jobs) { %>
            <tr>
                <td><%=r.get("id")%></td>
                <td><%=r.get("email")%></td>
                <td><%=r.get("title")%></td>
                <td><%=r.get("status")%></td>
                <td><form method="post" action="/admin/content/delete-job" style="margin:0">
                    <input type="hidden" name="id" value="<%=r.get("id")%>">
                    <button class="btn-sm" onclick="return confirm('Delete this job?')">Delete</button>
                </form></td>
            </tr>
            <% } %>
            </tbody>
        </table>

        <p class="section-title">Services (<%=services.size()%>)</p>
        <table class="admin-table">
            <thead><tr><th>ID</th><th>Owner</th><th>Title</th><th>Action</th></tr></thead>
            <tbody>
            <% for (Map<String,Object> r : services) { %>
            <tr>
                <td><%=r.get("id")%></td>
                <td><%=r.get("email")%></td>
                <td><%=r.get("title")%></td>
                <td><form method="post" action="/admin/content/delete-service" style="margin:0">
                    <input type="hidden" name="id" value="<%=r.get("id")%>">
                    <button class="btn-sm" onclick="return confirm('Delete this service?')">Delete</button>
                </form></td>
            </tr>
            <% } %>
            </tbody>
        </table>
        <footer class="mkv-footer" style="margin-top:60px;">© 2026 MKV Team</footer>
    </main>
</div>
</body>
</html>
