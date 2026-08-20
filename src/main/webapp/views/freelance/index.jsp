<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0"><title>Freelance Jobs - Student OS</title><link rel="stylesheet" href="/css/main.css">    <link rel="icon" type="image/png" href="/favicon.png">
</head>
<body><div class="app-container">
<aside class="sidebar"><div class="sidebar-logo">STUDENT OS</div>
<div class="nav-group"><div class="nav-title">My Life</div><a href="/dashboard" class="nav-link">Overview</a><a href="/goals" class="nav-link">Goals</a><a href="/schedule" class="nav-link">Schedule</a></div>
<div class="nav-group"><div class="nav-title">Skills</div><a href="/skills/discover" class="nav-link">Discover</a><a href="/skills" class="nav-link">My Skills</a></div>
<div class="nav-group"><div class="nav-title">Work</div><a href="/freelance" class="nav-link" style="background-color:var(--border-color)">Freelance Jobs</a><a href="/freelance/services" class="nav-link">Services</a></div>
<div style="margin-top:auto"><a href="/auth/logout" class="nav-link" style="color:var(--danger)">Logout</a></div></aside>
<main class="main-content"><header class="page-header"><div><h1 class="page-title">Job Marketplace</h1><p class="page-subtitle">Turn your skills into opportunities.</p></div><button class="btn btn-primary" type="button" onclick="document.getElementById('jobModal').style.display='flex'">+ Post Job</button></header>
<section class="grid-2"><c:choose><c:when test="${empty jobs}"><div class="card">No jobs are available right now.</div></c:when><c:otherwise><c:forEach var="job" items="${jobs}"><article class="card"><div class="card-header"><div class="card-title"><c:out value="${job.title}"/></div><span class="badge badge-completed">$<c:out value="${job.budget}"/></span></div><p style="color:var(--text-secondary)"><c:out value="${job.description}"/></p><small>Status: <c:out value="${job.status}"/></small></article></c:forEach></c:otherwise></c:choose></section>
<footer class="mkv-footer">© 2026 MKV Team</footer>
    </main></div>
<div id="jobModal" style="display:none;position:fixed;inset:0;background:rgba(0,0,0,.5);align-items:center;justify-content:center"><div style="background:var(--bg-primary);padding:32px;border-radius:12px;width:100%;max-width:500px"><h2>Post a Job</h2><form action="/freelance/post" method="post"><div class="form-group"><label class="form-label" for="jobTitle">Job Title</label><input id="jobTitle" name="title" class="form-control" required></div><div class="form-group"><label class="form-label" for="jobDescription">Description</label><textarea id="jobDescription" name="description" class="form-control" rows="4" required></textarea></div><div class="form-group"><label class="form-label" for="jobBudget">Budget ($)</label><input id="jobBudget" type="number" step="0.01" name="budget" class="form-control" required></div><button class="btn btn-primary" type="submit">Post Job</button></form></div></div>
</body></html>
