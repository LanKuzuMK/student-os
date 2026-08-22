<%-- Design direction: calm editorial control room with readable account governance and safe action states. --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*,com.studentos.model.User,com.studentos.util.HtmlUtil" %>
<%
    List<User> users = (List<User>) request.getAttribute("users");
    if (users == null) users = new ArrayList<>();
    String msg = request.getParameter("msg");
    String csrfToken = (String) request.getAttribute("csrfToken");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Users — Admin — StudentOS</title>
    <link rel="stylesheet" href="/css/main.css?v=admin-ui-20260822">
    <link rel="icon" type="image/png" href="/favicon.png">
</head>
<body>
<div class="admin-shell">
    <aside class="admin-rail">
        <div class="admin-rail-logo">StudentOS <span>Admin</span></div>
        <nav class="admin-nav" aria-label="Administrator navigation">
            <a href="/admin">Dashboard</a><a href="/admin/users" class="active">Users</a><a href="/admin/content">Content</a><a href="/admin/messages">Messages</a><a href="/admin/reports">Reports</a><a href="/admin/audit">Audit log</a><a href="/dashboard">Back to App</a>
        </nav>
        <div class="admin-rail-footer"><a href="/auth/logout">Logout</a></div>
    </aside>
    <main class="admin-main">
        <header class="admin-header">
            <div><p class="eyebrow">Account governance</p><h1>Member administration</h1><p>Review status, access role, and recovery actions for the StudentOS community.</p></div>
            <span class="admin-status"><%=users.size()%> accounts listed</span>
        </header>
        <% if (msg != null) { %><div class="toast">Action completed: <%=HtmlUtil.escapeHtml(msg.replace("_"," "))%></div><% } %>
        <div class="admin-table-wrap">
            <table class="admin-table">
                <thead><tr><th>ID</th><th>Email</th><th>Role</th><th>Status</th><th>Joined</th><th>Account actions</th></tr></thead>
                <tbody>
                <% for (User u : users) { %>
                <tr>
                    <td><%=u.getId()%></td><td><strong><%=HtmlUtil.escapeHtml(u.getEmail())%></strong></td>
                    <td><span class="badge <%="ADMIN".equals(u.getRole()) ? "badge-admin" : "badge-student"%>"><%=HtmlUtil.escapeHtml(u.getRole())%></span></td>
                    <td><span class="badge <%="BANNED".equals(u.getStatus()) ? "badge-banned" : "badge-active"%>"><%=HtmlUtil.escapeHtml(u.getStatus())%></span></td>
                    <td><%=u.getCreatedAt() != null ? u.getCreatedAt().toString().substring(0,10) : ""%></td>
                    <td><div class="action-row">
                        <% if ("BANNED".equals(u.getStatus())) { %><form method="post" action="/admin/users/unban"><input type="hidden" name="userId" value="<%=u.getId()%>"><input type="hidden" name="csrfToken" value="<%=csrfToken%>"><button class="btn-sm btn-unban">Unban</button></form>
                        <% } else { %><form method="post" action="/admin/users/ban"><input type="hidden" name="userId" value="<%=u.getId()%>"><input type="hidden" name="csrfToken" value="<%=csrfToken%>"><button class="btn-sm btn-ban" onclick="return confirm('Ban this user?')">Ban</button></form><% } %>
                        <button class="btn-sm btn-role" onclick="openRoleModal(<%=u.getId()%>,'<%=HtmlUtil.escapeJavaScript(u.getRole())%>')">Role</button>
                        <button class="btn-sm btn-reset" onclick="openResetModal(<%=u.getId()%>,'<%=HtmlUtil.escapeJavaScript(u.getEmail())%>')">Reset password</button>
                        <form method="post" action="/admin/users/delete"><input type="hidden" name="userId" value="<%=u.getId()%>"><input type="hidden" name="csrfToken" value="<%=csrfToken%>"><button class="btn-sm btn-delete" onclick="return confirm('Permanently delete this account and all its data?')">Delete</button></form>
                    </div></td>
                </tr><% } %>
                </tbody>
            </table>
        </div>
        <footer class="mkv-footer">© 2026 MKV Team</footer>
    </main>
</div>
<div class="modal-overlay" id="roleModal"><div class="modal"><h3>Change role</h3><form method="post" action="/admin/users/role"><input type="hidden" name="userId" id="roleUserId"><input type="hidden" name="csrfToken" value="<%=csrfToken%>"><select name="newRole" class="form-control" style="margin-bottom:14px"><option value="STUDENT">STUDENT</option><option value="MODERATOR">MODERATOR</option><option value="ADMIN">ADMIN</option></select><div class="modal-actions"><button type="button" class="btn-cancel" onclick="closeModals()">Cancel</button><button type="submit" class="btn-primary">Save role</button></div></form></div></div>
<div class="modal-overlay" id="resetModal"><div class="modal"><h3>Reset password for <span id="resetEmail"></span></h3><form method="post" action="/admin/users/reset-password"><input type="hidden" name="userId" id="resetUserId"><input type="hidden" name="csrfToken" value="<%=csrfToken%>"><input class="form-control" type="password" name="newPassword" placeholder="8+ characters, with a letter and number" required minlength="8" pattern="(?=.*[A-Za-z])(?=.*\d)\S{8,128}" title="Use 8 to 128 characters with a letter, a number, and no spaces." style="margin-bottom:14px"><div class="modal-actions"><button type="button" class="btn-cancel" onclick="closeModals()">Cancel</button><button type="submit" class="btn-primary">Reset password</button></div></form></div></div>
<script>
function openRoleModal(id, role) { document.getElementById('roleUserId').value = id; document.querySelector('#roleModal select').value = role; document.getElementById('roleModal').classList.add('open'); }
function openResetModal(id, email) { document.getElementById('resetUserId').value = id; document.getElementById('resetEmail').textContent = email; document.getElementById('resetModal').classList.add('open'); }
function closeModals() { document.querySelectorAll('.modal-overlay').forEach(m => m.classList.remove('open')); }
document.querySelectorAll('.modal-overlay').forEach(m => m.addEventListener('click', e => { if (e.target === m) closeModals(); }));
</script>
<script src="/js/admin-nav.js?v=20260822"></script>
</body>
</html>
