<%@ page import="java.util.*" %>
<%@ page import="com.studentos.util.HtmlUtil" %>
<%
List<Map<String,Object>> entries = (List<Map<String,Object>>) request.getAttribute("auditEntries");
%>
<!DOCTYPE html>
<html lang="en"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0"><title>Moderation audit - StudentOS</title><link rel="stylesheet" href="/css/main.css"><link rel="icon" type="image/png" href="/favicon.png"></head>
<body><div class="admin-shell"><aside class="admin-rail"><div class="admin-brand">StudentOS <span>Admin</span></div><nav><a href="/admin">Dashboard</a><a href="/admin/users">Users</a><a href="/admin/content">Content</a><a href="/admin/messages">Messages</a><a href="/admin/reports">Reports</a><a href="/admin/audit" class="active">Audit log</a><a href="/dashboard">Back to App</a></nav><div class="admin-rail-footer"><a href="/auth/logout">Logout</a></div></aside>
<main class="admin-main"><div class="admin-header"><div><p class="eyebrow">Accountability</p><h1>Moderation audit log</h1><p class="page-subtitle">The most recent administrative moderation actions, including the action reason where provided.</p></div><a href="/admin/reports" class="btn btn-secondary">Review reports</a></div>
<table class="admin-table"><thead><tr><th>When</th><th>Administrator</th><th>Action</th><th>Target</th><th>Reason</th></tr></thead><tbody><% for (Map<String,Object> entry : entries) { %><tr><td><%=entry.get("created_at")%></td><td><%=HtmlUtil.escapeHtml(entry.get("admin_email"))%></td><td><%=HtmlUtil.escapeHtml(entry.get("action"))%></td><td><%=HtmlUtil.escapeHtml(entry.get("target_type"))%> #<%=entry.get("target_id")%></td><td><%=HtmlUtil.escapeHtml(entry.get("reason"))%></td></tr><% } %></tbody></table><footer class="mkv-footer" style="margin-top:60px;">© 2026 MKV Team</footer></main></div></body></html>
