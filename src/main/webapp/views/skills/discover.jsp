<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0"><title>Discover Skills - Student OS</title><link rel="stylesheet" href="/css/main.css"></head>
<body><div class="app-container">
<aside class="sidebar"><div class="sidebar-logo">STUDENT OS</div>
<div class="nav-group"><div class="nav-title">My Life</div><a href="/dashboard" class="nav-link">Overview</a><a href="/goals" class="nav-link">Goals</a><a href="/schedule" class="nav-link">Schedule</a></div>
<div class="nav-group"><div class="nav-title">Community</div><a href="/skills/discover" class="nav-link" style="background-color:var(--border-color)">Discover talent</a><a href="/skills" class="nav-link">My skills</a><a href="/messages" class="nav-link">Messages</a></div>
<div class="nav-group"><div class="nav-title">Work</div><a href="/freelance" class="nav-link">Freelance Jobs</a><a href="/freelance/services" class="nav-link">Services</a></div>
<div style="margin-top:auto"><a href="/auth/logout" class="nav-link" style="color:var(--danger)">Logout</a></div></aside>
<main class="main-content"><header class="page-header"><div><h1 class="page-title">Discover Matches</h1><p class="page-subtitle">Find students to swap skills with.</p></div></header>
<section class="grid-2"><c:choose><c:when test="${empty allSkills}"><div class="card">No skills exist in the community yet.</div></c:when><c:otherwise><c:forEach var="skill" items="${allSkills}"><article class="card"><div class="card-header"><div class="card-title"><c:out value="${skill.skillName}"/></div><span class="badge badge-todo"><c:out value="${skill.type}"/></span></div><p style="color:var(--text-secondary)">Level: <c:out value="${skill.skillLevel}"/></p><p style="color:var(--text-secondary)">Student #<c:out value="${skill.userId}"/></p><a href="/messages?to=<c:out value='${skill.userId}'/>" class="btn btn-primary contact-btn">Message student <span aria-hidden="true">→</span></a></article></c:forEach></c:otherwise></c:choose></section>
</main></div></body></html>
