<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Profile - StudentOS</title>
    <link rel="stylesheet" href="/css/main.css?v=profile-studio-1">
    <link rel="icon" type="image/png" href="/favicon.png">
</head>
<body>
<div class="app-container">
    <aside class="sidebar">
        <div class="sidebar-logo"><span class="logo-mark">S</span> Student OS</div>
        <div class="nav-group"><div class="nav-title">Workspace</div><a href="/dashboard" class="nav-link">Overview</a><a href="/goals" class="nav-link">Goals</a><a href="/schedule" class="nav-link">Schedule</a></div>
        <div class="nav-group"><div class="nav-title">Community</div><a href="/skills/discover" class="nav-link">Discover talent</a><a href="/skills" class="nav-link">My skills</a><a href="/messages" class="nav-link">Messages<c:if test="${unreadMessageCount gt 0}"><span class="notification-badge"><c:out value="${unreadMessageCount}"/></span></c:if></a><a href="/profile" class="nav-link active">My profile</a></div>
        <div class="nav-group"><div class="nav-title">Opportunities</div><a href="/freelance" class="nav-link">Freelance jobs</a><a href="/freelance/services" class="nav-link">Services</a></div>
        <div class="sidebar-footer"><a href="/auth/logout" class="nav-link nav-link-danger">Log out</a></div>
    </aside>
    <main class="main-content">
        <header class="page-header profile-page-header">
            <div><p class="eyebrow">Student identity</p><h1 class="page-title">My profile</h1><p class="page-subtitle">Set the details classmates and collaborators see before they contact you.</p></div>
            <a class="btn btn-secondary" href="/profile/view?id=<c:out value='${profile.userId}'/>">View public profile</a>
        </header>

        <c:if test="${param.saved eq '1'}"><div class="alert alert-success">Your profile was updated successfully.</div></c:if>
        <c:if test="${param.error eq 'photo'}"><div class="alert alert-error">Upload a valid JPG or PNG image smaller than 5 MB.</div></c:if>
        <c:if test="${param.error eq 'save'}"><div class="alert alert-error">Your profile could not be saved. Please try again.</div></c:if>

        <form class="profile-editor" action="/profile/save" method="post" enctype="multipart/form-data">
            <section class="profile-identity-card">
                <div class="profile-avatar-editor">
                    <c:choose>
                        <c:when test="${profile.hasAvatar}"><img src="/profile/avatar?id=<c:out value='${profile.userId}'/>" alt="Your profile photo" class="profile-photo-lg"></c:when>
                        <c:otherwise><div class="profile-photo-placeholder">S</div></c:otherwise>
                    </c:choose>
                </div>
                <div class="profile-avatar-copy"><p class="section-kicker">Profile photo</p><h2>Keep it lightweight</h2><p>JPG and PNG uploads are converted to a compressed JPEG, scaled to a maximum of 256 pixels, and targeted below 75 kB.</p><label class="upload-control" for="avatar">Choose photo<input id="avatar" type="file" name="avatar" accept="image/jpeg,image/png"></label><small>Maximum original upload: 5 MB. Your existing photo is kept if you do not choose a new one.</small></div>
            </section>

            <section class="profile-form-card">
                <div class="profile-section-heading"><div><p class="section-kicker">Public details</p><h2>Introduce yourself</h2></div><p>Short and useful details make collaboration easier.</p></div>
                <div class="profile-field-grid">
                    <div class="form-group"><label class="form-label" for="firstName">First name</label><input class="form-control" id="firstName" name="firstName" maxlength="100" value="<c:out value='${profile.firstName}'/>" placeholder="Your first name"></div>
                    <div class="form-group"><label class="form-label" for="lastName">Last name</label><input class="form-control" id="lastName" name="lastName" maxlength="100" value="<c:out value='${profile.lastName}'/>" placeholder="Your last name"></div>
                    <div class="form-group"><label class="form-label" for="university">University</label><input class="form-control" id="university" name="university" maxlength="255" value="<c:out value='${profile.university}'/>" placeholder="Your university"></div>
                    <div class="form-group"><label class="form-label" for="major">Major or field</label><input class="form-control" id="major" name="major" maxlength="255" value="<c:out value='${profile.major}'/>" placeholder="For example, Computer Science"></div>
                </div>
                <div class="form-group"><label class="form-label" for="bio">Short description</label><textarea class="form-control" id="bio" name="bio" maxlength="500" rows="5" placeholder="What are you learning, building, or looking to collaborate on?"><c:out value="${profile.bio}"/></textarea><small>Up to 500 characters.</small></div>
            </section>

            <section class="profile-form-card">
                <div class="profile-section-heading"><div><p class="section-kicker">Links</p><h2>Where classmates can find you</h2></div><p>Only links you add here are public on your profile.</p></div>
                <div class="profile-field-grid">
                    <div class="form-group"><label class="form-label" for="portfolioUrl">Portfolio or website</label><input class="form-control" id="portfolioUrl" name="portfolioUrl" type="url" maxlength="500" value="<c:out value='${profile.portfolioUrl}'/>" placeholder="https://your-site.example"></div>
                    <div class="form-group"><label class="form-label" for="linkedinUrl">LinkedIn</label><input class="form-control" id="linkedinUrl" name="linkedinUrl" type="url" maxlength="500" value="<c:out value='${profile.linkedinUrl}'/>" placeholder="https://linkedin.com/in/your-name"></div>
                    <div class="form-group"><label class="form-label" for="telegramUrl">Telegram</label><input class="form-control" id="telegramUrl" name="telegramUrl" type="url" maxlength="500" value="<c:out value='${profile.telegramUrl}'/>" placeholder="https://t.me/your-name"></div>
                </div>
            </section>
            <div class="profile-save-row"><p>Your StudentOS email is shown only to signed-in students for collaboration.</p><button class="btn btn-primary" type="submit">Save profile</button></div>
        </form>
        <footer class="mkv-footer">© 2026 MKV Team</footer>
    </main>
</div>
</body>
</html>
