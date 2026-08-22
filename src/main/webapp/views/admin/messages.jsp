<%-- Design direction: calm editorial control room with clear activity scanning and deliberate destructive actions. --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*,com.studentos.util.HtmlUtil" %>
<%
    List<Map<String,Object>> messages = (List<Map<String,Object>>) request.getAttribute("messages");
    if (messages == null) messages = new ArrayList<>();
    String msg = request.getParameter("msg"); String csrfToken = (String) request.getAttribute("csrfToken");
%>
<!DOCTYPE html>
<html lang="en"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0"><title>Messages — Admin — StudentOS</title><link rel="stylesheet" href="/css/main.css?v=admin-ui-20260822"><link rel="icon" type="image/png" href="/favicon.png"></head>
<body><div class="admin-shell"><aside class="admin-rail"><div class="admin-rail-logo">StudentOS <span>Admin</span></div><nav class="admin-nav" aria-label="Administrator navigation"><a href="/admin">Dashboard</a><a href="/admin/users">Users</a><a href="/admin/content">Content</a><a href="/admin/messages" class="active">Messages</a><a href="/admin/reports">Reports</a><a href="/admin/audit">Audit log</a><a href="/dashboard">Back to App</a></nav><div class="admin-rail-footer"><a href="/auth/logout">Logout</a></div></aside>
<main class="admin-main"><header class="admin-header"><div><p class="eyebrow">Communication oversight</p><h1>Message overview</h1><p>Review platform conversation records when moderation and safety work requires it.</p></div><span class="admin-status"><%=messages.size()%> messages listed</span></header>
<% if (msg != null) { %><div class="toast">Message deleted.</div><% } %>
<div class="admin-table-wrap"><table class="admin-table"><thead><tr><th>ID</th><th>From</th><th>To</th><th>Preview</th><th>Sent</th><th>Action</th></tr></thead><tbody>
<% for (Map<String,Object> m : messages) { %><tr><td><%=m.get("id")%></td><td><strong><%=HtmlUtil.escapeHtml(m.get("sender"))%></strong></td><td><%=HtmlUtil.escapeHtml(m.get("receiver"))%></td><td class="preview-cell"><%=HtmlUtil.escapeHtml(m.get("preview"))%></td><td><%=m.get("created_at") != null ? m.get("created_at").toString().substring(0,16) : ""%></td><td><form method="post" action="/admin/messages/delete"><input type="hidden" name="id" value="<%=m.get("id")%>"><input type="hidden" name="csrfToken" value="<%=csrfToken%>"><button class="btn-sm btn-delete" onclick="return confirm('Permanently delete this message?')">Delete</button></form></td></tr><% } %>
</tbody></table></div><footer class="mkv-footer">© 2026 MKV Team</footer></main></div><script src="/js/admin-nav.js?v=20260822"></script></body></html>
