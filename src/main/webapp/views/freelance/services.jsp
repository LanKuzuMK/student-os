<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0"><title>Services - Student OS</title><link rel="stylesheet" href="/css/main.css">    <link rel="icon" type="image/png" href="/favicon.png">
</head>
<body><div class="app-container">
<aside class="sidebar"><div class="sidebar-logo">STUDENT OS</div>
<div class="nav-group"><div class="nav-title">My Life</div><a href="/dashboard" class="nav-link">Overview</a><a href="/goals" class="nav-link">Goals</a><a href="/schedule" class="nav-link">Schedule</a></div>
<div class="nav-group"><div class="nav-title">Skills</div><a href="/skills/discover" class="nav-link">Discover</a><a href="/skills" class="nav-link">My Skills</a></div>
<div class="nav-group"><div class="nav-title">Work</div><a href="/freelance" class="nav-link">Freelance Jobs</a><a href="/freelance/services" class="nav-link" style="background-color:var(--border-color)">Services</a></div>
<div style="margin-top:auto"><a href="/auth/logout" class="nav-link" style="color:var(--danger)">Logout</a></div></aside>
<main class="main-content"><header class="page-header"><div><h1 class="page-title">Services</h1><p class="page-subtitle">Offer skills and find help from other students.</p></div></header>
<section class="grid-2"><article class="card"><h3>Share a service</h3><p style="color:var(--text-secondary)">Create a service listing to offer your skills to the student community.</p><a href="/skills" class="btn btn-primary">Manage My Skills</a></article><article class="card"><h3>Find collaborators</h3><p style="color:var(--text-secondary)">Browse student skills to find people who can help with your projects.</p><a href="/skills/discover" class="btn btn-secondary">Discover Skills</a></article></section>
<footer class="mkv-footer">© 2026 MKV Team</footer>
    </main></div></body></html>
