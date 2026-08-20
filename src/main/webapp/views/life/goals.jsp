<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="theme-color" content="#5a5ce2">
    <title>Goals - StudentOS</title>
    <link rel="stylesheet" href="/css/main.css">
    <link rel="icon" type="image/png" href="/favicon.png">
</head>
<body>
<div class="app-container">
    <aside class="sidebar">
        <div class="sidebar-logo">STUDENT OS</div>
        <div class="nav-group"><div class="nav-title">My Life</div>
            <a href="/dashboard" class="nav-link">Overview</a><a href="/goals" class="nav-link active">Goals</a><a href="/schedule" class="nav-link">Schedule</a>
        </div>
        <div class="nav-group"><div class="nav-title">Skills</div>
            <a href="/skills/discover" class="nav-link">Discover</a><a href="/skills" class="nav-link">My Skills</a>
        </div>
        <div class="nav-group"><div class="nav-title">Work</div>
            <a href="/freelance" class="nav-link">Freelance Jobs</a><a href="/freelance/services" class="nav-link">Services</a>
        </div>
        <div class="sidebar-footer"><a href="/auth/logout" class="nav-link nav-link-danger">Log out</a></div>
    </aside>

    <main class="main-content">
        <header class="page-header goals-page-header">
            <div>
                <div class="eyebrow">Personal planning</div>
                <h1 class="page-title">Goals</h1>
                <p class="page-subtitle">Set a direction, break it into progress, and keep your next milestone visible.</p>
            </div>
            <div class="goals-count"><strong><c:out value="${empty goals ? 0 : goals.size()}"/></strong><span>tracked goals</span></div>
        </header>

        <section class="goal-workspace" aria-labelledby="new-goal-heading">
            <div class="goal-form-intro">
                <div class="section-kicker">Create a goal</div>
                <h2 id="new-goal-heading">Start with a clear outcome.</h2>
                <p>Write what you want to accomplish, add a short reason, then update the percentage as you make progress.</p>
                <div class="goal-guidance"><span>1. Define the outcome</span><span>2. Set your starting progress</span><span>3. Review it regularly</span></div>
            </div>
            <form class="goal-form" action="/goals" method="post">
                <div class="form-group"><label class="form-label" for="goalTitle">What do you want to achieve?</label><input id="goalTitle" class="form-control" name="title" maxlength="120" placeholder="e.g. Complete my Java web application" required></div>
                <div class="form-group"><label class="form-label" for="goalDescription">Why does this matter? <span class="form-label-optional">Optional</span></label><textarea id="goalDescription" class="form-control" name="description" rows="3" maxlength="500" placeholder="A short note to keep this goal meaningful and specific."></textarea></div>
                <div class="goal-progress-row"><div class="form-group"><label class="form-label" for="goalProgress">Current progress</label><div class="progress-input"><input id="goalProgress" class="form-control" type="number" name="progress" min="0" max="100" value="0" required><span>%</span></div></div><p class="goal-progress-help">Starting from zero is completely fine. Update the percentage as you move forward.</p></div>
                <button class="btn btn-primary" type="submit">Save goal</button>
            </form>
        </section>

        <section class="goal-list-section" aria-labelledby="tracked-goals-heading">
            <div class="section-heading"><div><div class="section-kicker">Your direction</div><h2 id="tracked-goals-heading">Tracked goals</h2></div><span class="section-heading-note">Keep the next step simple.</span></div>
            <c:choose>
                <c:when test="${empty goals}">
                    <div class="goals-empty-state">
                        <div class="goals-empty-line"></div>
                        <h3>No goals yet.</h3>
                        <p>Choose one thing you want to make progress on this semester, then save it above to begin tracking it.</p>
                        <button class="btn btn-secondary" type="button" onclick="document.getElementById('goalTitle').focus()">Add your first goal</button>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="goal-grid">
                        <c:forEach var="goal" items="${goals}">
                            <article class="goal-card">
                                <div class="goal-card-top"><span class="goal-status">In progress</span><strong><c:out value="${goal.progress}"/>%</strong></div>
                                <h3><c:out value="${goal.title}"/></h3>
                                <p><c:choose><c:when test="${empty goal.description}">Add a short note next time you review this goal.</c:when><c:otherwise><c:out value="${goal.description}"/></c:otherwise></c:choose></p>
                                <div class="goal-progress-bar" aria-label="<c:out value='${goal.progress}'/> percent complete"><span style="width:<c:out value='${goal.progress}'/>%"></span></div>
                                <div class="goal-card-footer"><span>Progress recorded</span><span><c:out value="${goal.progress}"/>% complete</span></div>
                            </article>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </section>
        <footer class="mkv-footer">© 2026 MKV Team</footer>
    </main>
</div>
</body>
</html>
