<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="theme-color" content="#5a5ce2">
    <title>Goals - StudentOS</title>
    <link rel="stylesheet" href="/css/main.css?v=mobile-nav-phone-5">
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
            <a href="/skills/discover" class="nav-link">Discover</a><a href="/skills" class="nav-link">My Skills</a><a href="/messages" class="nav-link">Messages</a><a href="/profile" class="nav-link">My profile</a>
        </div>
        <div class="nav-group"><div class="nav-title">Work</div>
            <a href="/freelance" class="nav-link">Freelance Jobs</a><a href="/freelance/services" class="nav-link">Services</a>
        </div>
        <div class="sidebar-footer"><a href="/auth/logout" class="nav-link nav-link-danger">Log out</a></div>
    </aside>

    <main class="main-content">
        <header class="page-header goals-toolbar">
            <div>
                <div class="eyebrow">Personal planning</div>
                <h1 class="page-title">Goals</h1>
                <p class="page-subtitle">Keep the outcomes that matter visible, measurable, and easy to update.</p>
            </div>
            <button class="btn btn-primary" type="button" onclick="document.getElementById('addGoalModal').style.display='flex'">Add goal</button>
        </header>

        <c:if test="${not empty param.success}"><div class="alert alert-success"><c:out value="${param.success}"/></div></c:if>
        <c:if test="${not empty param.error}"><div class="alert alert-error"><c:out value="${param.error}"/></div></c:if>

        <section class="goals-overview" aria-label="Goal summary">
            <div><span class="goals-overview-label">Tracked goals</span><strong><c:out value="${goalCount}"/></strong></div>
            <div class="goals-overview-copy"><strong>Manage goals your way.</strong><span>Use <b>Update progress</b> for quick changes, <b>Edit</b> to revise details, or <b>Delete</b> when a goal is no longer relevant.</span></div>
        </section>

        <section class="goal-manager" aria-labelledby="goals-heading">
            <div class="goal-manager-heading"><div><div class="section-kicker">Your goals</div><h2 id="goals-heading">What you are working toward</h2></div><span>Changes save immediately</span></div>
            <c:choose>
                <c:when test="${empty goals}">
                    <div class="goal-manager-empty">
                        <h3>No goals yet.</h3>
                        <p>Add one clear outcome, then return here to update the progress as you move forward.</p>
                        <button class="btn btn-secondary" type="button" onclick="document.getElementById('addGoalModal').style.display='flex'">Create your first goal</button>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="goal-manager-grid">
                        <c:forEach var="goal" items="${goals}">
                            <article class="managed-goal-card">
                                <div class="managed-goal-top"><div><span class="managed-goal-status"><c:choose><c:when test="${goal.progress eq 100}">Complete</c:when><c:otherwise>In progress</c:otherwise></c:choose></span><h3><c:out value="${goal.title}"/></h3></div><strong class="managed-goal-percent"><c:out value="${goal.progress}"/>%</strong></div>
                                <p class="managed-goal-description"><c:choose><c:when test="${empty goal.description}">No description yet. Use Edit to add context.</c:when><c:otherwise><c:out value="${goal.description}"/></c:otherwise></c:choose></p>
                                <div class="managed-goal-bar" aria-label="<c:out value='${goal.progress}'/> percent complete"><span style="width:<c:out value='${goal.progress}'/>%"></span></div>
                                <form class="managed-progress-form" action="/goals" method="post">
                                    <input type="hidden" name="action" value="progress"><input type="hidden" name="goalId" value="<c:out value='${goal.id}'/>">
                                    <label for="progress-<c:out value='${goal.id}'/>">Progress</label><div><input id="progress-<c:out value='${goal.id}'/>" class="form-control" type="number" name="progress" min="0" max="100" value="<c:out value='${goal.progress}'/>" required><span>%</span></div><button class="btn btn-secondary" type="submit">Update progress</button>
                                </form>
                                <div class="managed-goal-actions">
                                    <button class="text-action" type="button" onclick="document.getElementById('editGoal-<c:out value='${goal.id}'/>').style.display='flex'">Edit</button>
                                    <form action="/goals" method="post" onsubmit="return confirm('Delete this goal? This cannot be undone.');"><input type="hidden" name="action" value="delete"><input type="hidden" name="goalId" value="<c:out value='${goal.id}'/>"><button class="text-action text-action-danger" type="submit">Delete</button></form>
                                </div>
                            </article>

                            <div id="editGoal-<c:out value='${goal.id}'/>" class="goal-modal" style="display:none;">
                                <div class="goal-modal-card" role="dialog" aria-modal="true" aria-labelledby="edit-title-<c:out value='${goal.id}'/>">
                                    <div class="goal-modal-header"><div><div class="section-kicker">Edit goal</div><h2 id="edit-title-<c:out value='${goal.id}'/>">Update your goal</h2></div><button class="modal-close" type="button" aria-label="Close" onclick="document.getElementById('editGoal-<c:out value='${goal.id}'/>').style.display='none'">×</button></div>
                                    <form action="/goals" method="post"><input type="hidden" name="action" value="update"><input type="hidden" name="goalId" value="<c:out value='${goal.id}'/>">
                                        <div class="form-group"><label class="form-label" for="edit-title-input-<c:out value='${goal.id}'/>">Goal title</label><input id="edit-title-input-<c:out value='${goal.id}'/>" class="form-control" name="title" maxlength="120" value="<c:out value='${goal.title}'/>" required></div>
                                        <div class="form-group"><label class="form-label" for="edit-description-<c:out value='${goal.id}'/>">Description</label><textarea id="edit-description-<c:out value='${goal.id}'/>" class="form-control" name="description" rows="3" maxlength="500"><c:out value="${goal.description}"/></textarea></div>
                                        <div class="form-group"><label class="form-label" for="edit-progress-<c:out value='${goal.id}'/>">Progress</label><div class="progress-input"><input id="edit-progress-<c:out value='${goal.id}'/>" class="form-control" type="number" name="progress" min="0" max="100" value="<c:out value='${goal.progress}'/>" required><span>%</span></div></div>
                                        <div class="modal-actions"><button class="btn btn-secondary" type="button" onclick="document.getElementById('editGoal-<c:out value='${goal.id}'/>').style.display='none'">Cancel</button><button class="btn btn-primary" type="submit">Save changes</button></div>
                                    </form>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </section>
        <footer class="mkv-footer">© 2026 MKV Team</footer>
    </main>
</div>

<div id="addGoalModal" class="goal-modal" style="display:none;">
    <div class="goal-modal-card" role="dialog" aria-modal="true" aria-labelledby="add-goal-title">
        <div class="goal-modal-header"><div><div class="section-kicker">New goal</div><h2 id="add-goal-title">Add a goal</h2></div><button class="modal-close" type="button" aria-label="Close" onclick="document.getElementById('addGoalModal').style.display='none'">×</button></div>
        <form action="/goals" method="post"><input type="hidden" name="action" value="create">
            <div class="form-group"><label class="form-label" for="newGoalTitle">What do you want to achieve?</label><input id="newGoalTitle" class="form-control" name="title" maxlength="120" placeholder="e.g. Complete my Java web application" required></div>
            <div class="form-group"><label class="form-label" for="newGoalDescription">Description <span class="form-label-optional">Optional</span></label><textarea id="newGoalDescription" class="form-control" name="description" rows="3" maxlength="500" placeholder="A short note that explains why this matters."></textarea></div>
            <div class="form-group"><label class="form-label" for="newGoalProgress">Starting progress</label><div class="progress-input"><input id="newGoalProgress" class="form-control" type="number" name="progress" min="0" max="100" value="0" required><span>%</span></div></div>
            <div class="modal-actions"><button class="btn btn-secondary" type="button" onclick="document.getElementById('addGoalModal').style.display='none'">Cancel</button><button class="btn btn-primary" type="submit">Save goal</button></div>
        </form>
    </div>
</div>
    <script src="/js/mobile-nav.js?v=mobile-nav-phone-5" defer></script>
</body>
</html>
