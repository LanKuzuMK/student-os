<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Admin Dashboard - Student OS</title>
    <link rel="stylesheet" href="/css/main.css">
    <style>
        .stat-card {
            background-color: var(--bg-primary); border: 1px solid var(--border-color);
            border-radius: var(--radius); padding: 32px; text-align: center;
        }
        .stat-value { font-size: 36px; font-weight: 700; margin-bottom: 8px; }
        .stat-label { color: var(--text-secondary); font-size: 14px; text-transform: uppercase; letter-spacing: 1px; }
    </style>
    <link rel="icon" type="image/png" href="/favicon.png">
</head>
<body>
    <div class="app-container">
        <!-- Sidebar -->
        <aside class="sidebar" style="background-color: #0f172a; color: white;">
            <div class="sidebar-logo" style="color: white;">STUDENT OS <span style="color:var(--danger); font-size: 12px;">ADMIN</span></div>
            <div class="nav-group">
                <a href="/dashboard" class="nav-link" style="color: white;">Back to App</a>
                <a href="/admin" class="nav-link" style="background-color: rgba(255,255,255,0.1); color: white;">Dashboard</a>
            </div>
            <div style="margin-top: auto;">
                <a href="/auth/logout" class="nav-link" style="color: var(--danger);">Logout</a>
            </div>
        </aside>

        <!-- Main Content -->
        <main class="main-content">
            <header class="page-header">
                <div>
                    <h1 class="page-title">Platform Analytics</h1>
                    <p class="page-subtitle">Overview of Student OS ecosystem.</p>
                </div>
            </header>

            <section class="grid-2">
                <div class="stat-card">
                    <div class="stat-value">\</div>
                    <div class="stat-label">Total Users</div>
                </div>
                <div class="stat-card">
                    <div class="stat-value">\</div>
                    <div class="stat-label">Skills Registered</div>
                </div>
                <div class="stat-card">
                    <div class="stat-value">\</div>
                    <div class="stat-label">Freelance Jobs</div>
                </div>
            </section>
        <footer class="mkv-footer">© 2026 MKV Team</footer>
    </main>
    </div>
</body>
</html>
