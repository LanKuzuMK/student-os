<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*,com.studentos.model.User" %>
<%
    List<User> users = (List<User>) request.getAttribute("users");
    if (users == null) users = new ArrayList<>();
    String msg = request.getParameter("msg");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Users — Admin — StudentOS</title>
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
        .admin-table { width:100%; border-collapse:collapse; background:#fff; border:1px solid #e2e8f0; border-radius:12px; overflow:hidden; font-size:13px; }
        .admin-table th { background:#f8fafc; padding:12px 14px; text-align:left; color:#64748b; font-size:11px; font-weight:700; letter-spacing:.07em; text-transform:uppercase; border-bottom:1px solid #e2e8f0; }
        .admin-table td { padding:12px 14px; border-bottom:1px solid #f1f5f9; vertical-align:middle; }
        .admin-table tr:last-child td { border-bottom:0; }
        .badge { display:inline-block; padding:3px 9px; border-radius:999px; font-size:11px; font-weight:700; }
        .badge-admin   { background:#ede9fe; color:#6d28d9; }
        .badge-student { background:#e0f2fe; color:#0369a1; }
        .badge-banned  { background:#fee2e2; color:#dc2626; }
        .badge-active  { background:#dcfce7; color:#166534; }
        .action-row { display:flex; gap:6px; flex-wrap:wrap; }
        .btn-sm { padding:5px 10px; border:0; border-radius:7px; font-size:11px; font-weight:700; cursor:pointer; }
        .btn-ban    { background:#fee2e2; color:#dc2626; }
        .btn-unban  { background:#dcfce7; color:#166534; }
        .btn-delete { background:#fef3c7; color:#92400e; }
        .btn-role   { background:#ede9fe; color:#6d28d9; }
        .btn-reset  { background:#e0f2fe; color:#0369a1; }
        .modal-overlay { display:none; position:fixed; inset:0; background:rgba(0,0,0,.4); z-index:100; align-items:center; justify-content:center; }
        .modal-overlay.open { display:flex; }
        .modal { background:#fff; border-radius:16px; padding:28px; width:min(400px,90vw); }
        .modal h3 { margin:0 0 16px; font-family:'Space Grotesk',sans-serif; font-size:18px; }
        .modal input { width:100%; padding:10px 12px; border:1px solid #e2e8f0; border-radius:8px; font-size:14px; margin-bottom:14px; box-sizing:border-box; }
        .modal-actions { display:flex; gap:10px; justify-content:flex-end; }
        .btn-primary { padding:9px 18px; border:0; border-radius:8px; background:#4f46e5; color:#fff; font-size:13px; font-weight:700; cursor:pointer; }
        .btn-cancel  { padding:9px 18px; border:1px solid #e2e8f0; border-radius:8px; background:#fff; font-size:13px; font-weight:600; cursor:pointer; }
    </style>
</head>
<body>
<div class="admin-shell">
    <aside class="admin-rail">
        <div class="admin-rail-logo">StudentOS <span>ADMIN</span></div>
        <nav class="admin-nav">
            <a href="/admin">Dashboard</a>
            <a href="/admin/users" class="active">Users</a>
            <a href="/admin/content">Content</a>
            <a href="/admin/messages">Messages</a>
            <a href="/dashboard">Back to App</a>
        </nav>
        <div class="admin-rail-footer"><a href="/auth/logout">Logout</a></div>
    </aside>
    <main class="admin-main">
        <div class="admin-header"><h1>User Management</h1></div>
        <% if (msg != null) { %>
        <div class="toast">Action completed: <%=msg.replace("_"," ")%></div>
        <% } %>
        <table class="admin-table">
            <thead>
                <tr>
                    <th>ID</th><th>Email</th><th>Role</th><th>Status</th><th>Joined</th><th>Actions</th>
                </tr>
            </thead>
            <tbody>
            <% for (User u : users) { %>
            <tr>
                <td><%=u.getId()%></td>
                <td><%=u.getEmail()%></td>
                <td><span class="badge <%="ADMIN".equals(u.getRole())?"badge-admin":"badge-student"%>"><%=u.getRole()%></span></td>
                <td><span class="badge <%="BANNED".equals(u.getStatus())?"badge-banned":"badge-active"%>"><%=u.getStatus()%></span></td>
                <td><%=u.getCreatedAt() != null ? u.getCreatedAt().toString().substring(0,10) : ""%></td>
                <td>
                    <div class="action-row">
                        <% if ("BANNED".equals(u.getStatus())) { %>
                        <form method="post" action="/admin/users/unban" style="margin:0">
                            <input type="hidden" name="userId" value="<%=u.getId()%>">
                            <button class="btn-sm btn-unban">Unban</button>
                        </form>
                        <% } else { %>
                        <form method="post" action="/admin/users/ban" style="margin:0">
                            <input type="hidden" name="userId" value="<%=u.getId()%>">
                            <button class="btn-sm btn-ban" onclick="return confirm('Ban this user?')">Ban</button>
                        </form>
                        <% } %>
                        <button class="btn-sm btn-role"
                            onclick="openRoleModal(<%=u.getId()%>,'<%=u.getRole()%>')">Role</button>
                        <button class="btn-sm btn-reset"
                            onclick="openResetModal(<%=u.getId()%>,'<%=u.getEmail()%>')">Reset PW</button>
                        <form method="post" action="/admin/users/delete" style="margin:0">
                            <input type="hidden" name="userId" value="<%=u.getId()%>">
                            <button class="btn-sm btn-delete"
                                onclick="return confirm('Permanently delete this account and all its data?')">Delete</button>
                        </form>
                    </div>
                </td>
            </tr>
            <% } %>
            </tbody>
        </table>
        <footer class="mkv-footer" style="margin-top:60px;">© 2026 MKV Team</footer>
    </main>
</div>

<!-- Role modal -->
<div class="modal-overlay" id="roleModal">
    <div class="modal">
        <h3>Change Role</h3>
        <form method="post" action="/admin/users/role">
            <input type="hidden" name="userId" id="roleUserId">
            <select name="newRole" style="width:100%;padding:10px;border:1px solid #e2e8f0;border-radius:8px;font-size:14px;margin-bottom:14px;">
                <option value="STUDENT">STUDENT</option>
                <option value="ADMIN">ADMIN</option>
            </select>
            <div class="modal-actions">
                <button type="button" class="btn-cancel" onclick="closeModals()">Cancel</button>
                <button type="submit" class="btn-primary">Save</button>
            </div>
        </form>
    </div>
</div>

<!-- Password reset modal -->
<div class="modal-overlay" id="resetModal">
    <div class="modal">
        <h3>Reset Password for <span id="resetEmail"></span></h3>
        <form method="post" action="/admin/users/reset-password">
            <input type="hidden" name="userId" id="resetUserId">
            <input type="password" name="newPassword" placeholder="New password (min 6 chars)" required minlength="6">
            <div class="modal-actions">
                <button type="button" class="btn-cancel" onclick="closeModals()">Cancel</button>
                <button type="submit" class="btn-primary">Reset</button>
            </div>
        </form>
    </div>
</div>

<script>
function openRoleModal(id, role) {
    document.getElementById('roleUserId').value = id;
    document.querySelector('#roleModal select').value = role;
    document.getElementById('roleModal').classList.add('open');
}
function openResetModal(id, email) {
    document.getElementById('resetUserId').value = id;
    document.getElementById('resetEmail').textContent = email;
    document.getElementById('resetModal').classList.add('open');
}
function closeModals() {
    document.querySelectorAll('.modal-overlay').forEach(m => m.classList.remove('open'));
}
document.querySelectorAll('.modal-overlay').forEach(m =>
    m.addEventListener('click', e => { if (e.target === m) closeModals(); }));
</script>
</body>
</html>
