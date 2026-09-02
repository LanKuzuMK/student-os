<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Profile - StudentOS</title>
    <link rel="stylesheet" href="/css/main.css?v=mobile-nav-phone-6">
    <link rel="icon" type="image/png" href="/favicon.png">
</head>
<body>
<div class="app-container">
    <aside class="sidebar">
        <div class="sidebar-logo"><span class="logo-mark">S</span> Student OS</div>
        <div class="nav-group"><div class="nav-title">Workspace</div><a href="/dashboard" class="nav-link">Overview</a><a href="/goals" class="nav-link">Goals</a><a href="/schedule" class="nav-link">Schedule</a></div>
        <div class="nav-group"><div class="nav-title">Community</div><a href="/skills/discover" class="nav-link">Discover talent</a><a href="/skills" class="nav-link">My skills</a><a href="/collaborations" class="nav-link">Collaborations</a><a href="/messages" class="nav-link">Messages<c:if test="${unreadMessageCount gt 0}"><span class="notification-badge"><c:out value="${unreadMessageCount}"/></span></c:if></a><a href="/notifications" class="nav-link">Notifications<c:if test="${unreadNotificationCount gt 0}"><span class="notification-badge"><c:out value="${unreadNotificationCount}"/></span></c:if></a><a href="/profile" class="nav-link active">My profile</a></div>
        <div class="nav-group"><div class="nav-title">Opportunities</div><a href="/freelance" class="nav-link">Freelance jobs</a><a href="/freelance/services" class="nav-link">Services</a></div>
        <div class="sidebar-footer"><a href="/auth/logout" class="nav-link nav-link-danger">Log out</a></div>
    </aside>
    <main class="main-content">
        <header class="page-header profile-page-header">
            <div><p class="eyebrow">Student identity</p><h1 class="page-title">My profile</h1><p class="page-subtitle">Set the details classmates and collaborators see before they contact you.</p></div>
            <div class="page-header-actions"><a class="btn btn-secondary" href="/account/password">Change password</a><a class="btn btn-secondary" href="/profile/view?id=<c:out value='${profile.userId}'/>">View public profile</a></div>
        </header>

        <style>
        .progress-bar-container { width: 100%; height: 4px; background-color: #e5eaf1; border-radius: 4px; overflow: hidden; position: relative; display: none; margin-top: 10px; }
        .progress-bar-indicator { height: 100%; width: 30%; background-color: #4f55c8; border-radius: 4px; position: absolute; left: -30%; animation: indeterminate 1.5s infinite linear; }
        @keyframes indeterminate { 0% { left: -30%; width: 30%; } 50% { width: 30%; } 100% { left: 100%; width: 30%; } }
        .toast-notification { position: fixed; bottom: 24px; right: 24px; background: #101828; color: #fff; padding: 16px 24px; border-radius: 12px; box-shadow: 0 12px 24px rgba(0,0,0,0.15); display: flex; align-items: center; gap: 12px; z-index: 9999; opacity: 0; transform: translateY(20px); transition: opacity 0.3s ease, transform 0.3s ease; }
        .toast-notification.show { opacity: 1; transform: translateY(0); }
        .toast-icon { width: 24px; height: 24px; background: #12b76a; border-radius: 50%; display: flex; align-items: center; justify-content: center; }
        .toast-icon svg { width: 14px; height: 14px; fill: none; stroke: #fff; stroke-width: 2; stroke-linecap: round; stroke-linejoin: round; }
        </style>

        <c:if test="${param.saved eq '1'}">
            <div id="successToast" class="toast-notification">
                <div class="toast-icon">
                    <svg viewBox="0 0 24 24"><polyline points="20 6 9 17 4 12"></polyline></svg>
                </div>
                <span style="font-weight: 500; font-size: 15px;">Successfully uploaded</span>
            </div>
            <script>
                document.addEventListener("DOMContentLoaded", function() {
                    const toast = document.getElementById("successToast");
                    if (toast) {
                        setTimeout(() => toast.classList.add("show"), 100);
                        setTimeout(() => {
                            toast.classList.remove("show");
                            setTimeout(() => toast.remove(), 300);
                        }, 4000);
                    }
                });
            </script>
        </c:if>
        <c:if test="${param.error eq 'photo'}"><div class="alert alert-error">Upload a valid JPG or PNG image smaller than 5 MB.</div></c:if>
        <c:if test="${param.error eq 'save'}"><div class="alert alert-error">Your profile could not be saved. Please try again.</div></c:if>
        <c:if test="${param.linkAdded eq '1'}"><div class="alert alert-success">Your custom link was added.</div></c:if>
        <c:if test="${param.linkAdded eq '0'}"><div class="alert alert-error">Your custom link could not be added. Please try again.</div></c:if>
        <c:if test="${param.linkDeleted eq '1'}"><div class="alert alert-success">The link was permanently deleted.</div></c:if>
        <c:if test="${param.linkDeleted eq '0' or param.error eq 'linkDelete'}"><div class="alert alert-error">That link could not be deleted.</div></c:if>
        <c:if test="${param.error eq 'link'}"><div class="alert alert-error">Enter a link name and a valid URL beginning with http:// or https://.</div></c:if>
        <c:if test="${param.projectAdded eq '1'}"><div class="alert alert-success">Your project card was added.</div></c:if>
        <c:if test="${param.projectDeleted eq '1'}"><div class="alert alert-success">The project card was permanently deleted.</div></c:if>
        <c:if test="${param.error eq 'project' or param.error eq 'projectDelete'}"><div class="alert alert-error">Enter a project name and a valid http:// or https:// project link.</div></c:if>
        <c:if test="${param.avatarDeleted eq '1'}"><div class="alert alert-success">Your profile photo was deleted successfully.</div></c:if>
        <c:if test="${param.error eq 'avatarDelete'}"><div class="alert alert-error">Your profile photo could not be deleted.</div></c:if>

        <form id="deleteAvatarForm" action="/profile/avatar/delete" method="post" style="display: none;">
            <input type="hidden" name="csrfToken" value="<c:out value='${csrfToken}'/>">
        </form>

        <form class="profile-editor" action="/profile/save?csrfToken=<c:out value='${csrfToken}'/>" method="post" enctype="multipart/form-data">
            <section class="profile-form-card">
                <div class="profile-section-heading"><div><p class="section-kicker">Profile picture</p><h2>Put a face to your code</h2></div><p>A friendly, clear photo helps classmates recognize you on campus.</p></div>
                <div class="profile-field-avatar">
                    <c:choose>
                        <c:when test="${profile.hasAvatar}">
                            <img src="/profile/avatar?id=<c:out value='${profile.userId}'/>" alt="Current avatar" style="width: 80px; height: 80px; border-radius: 50%; object-fit: cover;">
                        </c:when>
                        <c:otherwise>
                            <div class="avatar-placeholder" style="width: 80px; height: 80px; border-radius: 50%; background: #eef0fb; color: #4e58bf; display: flex; align-items: center; justify-content: center; font-weight: bold; font-size: 24px;">
                                <c:out value="${empty profile.firstName ? '?' : profile.firstName.substring(0,1)}"/>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
                <div class="profile-field-avatar-text">
                    <p>JPG and PNG uploads are converted to a compressed JPEG, scaled to a maximum of 256 pixels, and targeted below 75 kB.</p>
                    
                    <c:choose>
                        <c:when test="${profile.hasAvatar}">
                            <div style="display: flex; gap: 12px; align-items: center; margin-bottom: 8px;">
                                <label class="upload-control" for="avatar">Change photo<input id="avatar" type="file" name="avatar" accept="image/jpeg,image/png"></label>
                                <button type="submit" form="deleteAvatarForm" class="btn btn-secondary" style="margin: 0; height: 38px; line-height: 1;" onclick="return confirm('Are you sure you want to permanently delete your profile photo?');">Delete photo</button>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <label class="upload-control" for="avatar">Choose photo<input id="avatar" type="file" name="avatar" accept="image/jpeg,image/png"></label>
                        </c:otherwise>
                    </c:choose>
                    
                    <small>Maximum original upload: 5 MB. Your existing photo is kept if you do not choose a new one.</small>
                </div>
            </section>

            <section class="profile-form-card">
                <div class="profile-section-heading"><div><p class="section-kicker">Basic info</p><h2>Who you are</h2></div><p>Tell the community what you're studying and what you're good at.</p></div>
                <div class="profile-field-grid">
                    <div class="form-group"><label class="form-label" for="firstName">First name</label><input class="form-control" id="firstName" name="firstName" maxlength="100" required value="<c:out value='${profile.firstName}'/>"></div>
                    <div class="form-group"><label class="form-label" for="lastName">Last name</label><input class="form-control" id="lastName" name="lastName" maxlength="100" required value="<c:out value='${profile.lastName}'/>"></div>
                    <div class="form-group"><label class="form-label" for="university">University</label><input class="form-control" id="university" name="university" maxlength="255" required value="<c:out value='${profile.university}'/>"></div>
                    <div class="form-group"><label class="form-label" for="major">Major or program</label><input class="form-control" id="major" name="major" maxlength="255" required value="<c:out value='${profile.major}'/>"></div>
                </div>
                <div class="form-group"><label class="form-label" for="bio">Short description</label><textarea class="form-control" id="bio" name="bio" maxlength="500" rows="5" placeholder="What are you learning, building, or looking to collaborate on?"><c:out value="${profile.bio}"/></textarea><small>Up to 500 characters.</small></div>
                <div class="profile-field-grid"><div class="form-group"><label class="form-label" for="availabilityStatus">Availability</label><select id="availabilityStatus" name="availabilityStatus" class="form-control"><option value="">Choose later</option><option value="OPEN_TO_COLLABORATE" <c:if test="${profile.availabilityStatus eq 'OPEN_TO_COLLABORATE'}">selected</c:if>>Open to collaborate</option><option value="LOOKING_FOR_TEAM" <c:if test="${profile.availabilityStatus eq 'LOOKING_FOR_TEAM'}">selected</c:if>>Looking for a team</option><option value="AVAILABLE_FOR_FREELANCE" <c:if test="${profile.availabilityStatus eq 'AVAILABLE_FOR_FREELANCE'}">selected</c:if>>Available for freelance work</option><option value="FOCUSED_ON_STUDY" <c:if test="${profile.availabilityStatus eq 'FOCUSED_ON_STUDY'}">selected</c:if>>Focused on study</option></select></div><div class="form-group"><label class="form-label" for="collaborationPreferences">Collaboration preferences</label><textarea class="form-control" id="collaborationPreferences" name="collaborationPreferences" maxlength="500" rows="3" placeholder="For example: weekend study groups, UI design projects, or short freelance tasks."><c:out value="${profile.collaborationPreferences}"/></textarea></div></div>
            </section>

            <section class="profile-form-card">
                <div class="profile-section-heading"><div><p class="section-kicker">Links</p><h2>Where classmates can find you</h2></div><p>Only links you add here are public on your profile.</p></div>
                <div class="profile-field-grid">
                    <div class="form-group"><label class="form-label" for="portfolioUrl">Portfolio or website</label><input class="form-control" id="portfolioUrl" name="portfolioUrl" type="url" maxlength="500" value="<c:out value='${profile.portfolioUrl}'/>" placeholder="https://your-site.example"></div>
                    <div class="form-group"><label class="form-label" for="linkedinUrl">LinkedIn</label><input class="form-control" id="linkedinUrl" name="linkedinUrl" type="url" maxlength="500" value="<c:out value='${profile.linkedinUrl}'/>" placeholder="https://linkedin.com/in/your-name"></div>
                    <div class="form-group"><label class="form-label" for="telegramUrl">Telegram</label><input class="form-control" id="telegramUrl" name="telegramUrl" type="url" maxlength="500" value="<c:out value='${profile.telegramUrl}'/>" placeholder="https://t.me/your-name"></div>
                </div>
            </section>
            <div class="profile-save-row">
                <div style="flex: 1;">
                    <p>Your StudentOS email is shown only to signed-in students for collaboration.</p>
                    <div id="uploadProgress" class="progress-bar-container"><div class="progress-bar-indicator"></div></div>
                </div>
                <button class="btn btn-primary" type="submit">Save profile</button>
            </div>
        </form>
        <script>
            document.querySelector('.profile-editor').addEventListener('submit', function() {
                document.getElementById('uploadProgress').style.display = 'block';
                const submitBtn = this.querySelector('button[type="submit"]');
                setTimeout(() => {
                    submitBtn.disabled = true;
                    submitBtn.textContent = 'Saving...';
                }, 0);
            });
        </script>

        <section class="profile-form-card profile-link-manager">
            <div class="profile-section-heading"><div><p class="section-kicker">Custom links</p><h2>Add any platform</h2></div><p>Add GitHub, Instagram, Behance, Discord, or any public social-media link classmates can use to find you.</p></div>
            <form class="custom-link-add-form" action="/profile/links/add" method="post">
                <input type="hidden" name="csrfToken" value="<c:out value='${csrfToken}'/>">
                <div class="form-group"><label class="form-label" for="linkLabel">Link name</label><input class="form-control" id="linkLabel" name="linkLabel" maxlength="80" required placeholder="For example, GitHub or Instagram"></div>
                <div class="form-group"><label class="form-label" for="linkUrl">Link URL</label><input class="form-control" id="linkUrl" name="linkUrl" type="url" maxlength="500" required placeholder="https://github.com/your-name"></div>
                <button class="btn btn-primary" type="submit">Add link</button>
            </form>
            <div class="custom-link-list">
                <c:choose>
                    <c:when test="${not empty profileLinks}">
                        <c:forEach items="${profileLinks}" var="customLink">
                            <article class="custom-profile-link-row">
                                <a href="<c:out value='${customLink.url}'/>" target="_blank" rel="noopener noreferrer"><strong><c:out value="${customLink.label}"/></strong><span><c:out value="${customLink.url}"/></span></a>
                                <form action="/profile/links/delete" method="post" onsubmit="return confirm('Permanently delete this link from your profile?');"><input type="hidden" name="linkId" value="<c:out value='${customLink.id}'/>"><input type="hidden" name="csrfToken" value="<c:out value='${csrfToken}'/>"><button class="text-action text-action-danger" type="submit">Delete</button></form>
                            </article>
                        </c:forEach>
                    </c:when>
                    <c:otherwise><p class="profile-empty-copy custom-link-empty">No custom links yet. Add one above to show it on your public profile.</p></c:otherwise>
                </c:choose>
            </div>
        </section>

        <section class="profile-form-card profile-link-manager">
            <div class="profile-section-heading"><div><p class="section-kicker">Project cards</p><h2>Show work, not just a link</h2></div><p>Add a compact public project card with a title, optional description, and external demo or repository link.</p></div>
            <form class="custom-link-add-form" action="/profile/projects/add" method="post">
                <input type="hidden" name="csrfToken" value="<c:out value='${csrfToken}'/>">
                <div class="form-group"><label class="form-label" for="projectTitle">Project title</label><input class="form-control" id="projectTitle" name="projectTitle" maxlength="120" required placeholder="For example, Campus Study Planner"></div>
                <div class="form-group"><label class="form-label" for="projectUrl">Project link</label><input class="form-control" id="projectUrl" name="projectUrl" type="url" maxlength="500" required placeholder="https://github.com/your-name/project"></div>
                <div class="form-group"><label class="form-label" for="projectDescription">Short description</label><input class="form-control" id="projectDescription" name="projectDescription" maxlength="500" placeholder="What you built, learned, or contributed"></div>
                <button class="btn btn-primary" type="submit">Add project</button>
            </form>
            <div class="custom-link-list"><c:choose><c:when test="${not empty profileProjects}"><c:forEach items="${profileProjects}" var="project"><article class="custom-profile-link-row"><a href="<c:out value='${project.url}'/>" target="_blank" rel="noopener noreferrer"><strong><c:out value="${project.title}"/></strong><span><c:out value="${project.description}"/></span></a><form action="/profile/projects/delete" method="post" onsubmit="return confirm('Permanently delete this project card?');"><input type="hidden" name="projectId" value="<c:out value='${project.id}'/>"><input type="hidden" name="csrfToken" value="<c:out value='${csrfToken}'/>"><button class="text-action text-action-danger" type="submit">Delete</button></form></article></c:forEach></c:when><c:otherwise><p class="profile-empty-copy custom-link-empty">No project cards yet. Add your best work above.</p></c:otherwise></c:choose></div>
        </section>
        <footer class="mkv-footer">© 2026 MKV Team</footer>
    </main>
</div>
    <script src="/js/mobile-nav.js?v=mobile-nav-phone-6" defer></script>
</body>
</html>
