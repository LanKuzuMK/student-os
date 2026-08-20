package com.studentos.controller;

import com.studentos.dao.ProfileDAO;
import com.studentos.model.Profile;
import com.studentos.model.User;
import com.studentos.util.AvatarCompressor;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.InputStream;

@WebServlet("/profile/*")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 5 * 1024 * 1024, maxRequestSize = 6 * 1024 * 1024)
public class ProfileController extends HttpServlet {
    private final ProfileDAO profileDAO = new ProfileDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User signedInUser = getSignedInUser(request, response);
        if (signedInUser == null) {
            return;
        }

        String path = request.getPathInfo();
        if ("/avatar".equals(path)) {
            serveAvatar(request, response);
            return;
        }
        if ("/view".equals(path)) {
            showPublicProfile(request, response, signedInUser);
            return;
        }

        request.setAttribute("profile", profileDAO.getByUserId(signedInUser.getId()));
        request.getRequestDispatcher("/views/profile/manage.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User signedInUser = getSignedInUser(request, response);
        if (signedInUser == null) {
            return;
        }
        if (!"/save".equals(request.getPathInfo())) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        Profile profile = new Profile();
        profile.setUserId(signedInUser.getId());
        profile.setFirstName(limit(request.getParameter("firstName"), 100));
        profile.setLastName(limit(request.getParameter("lastName"), 100));
        profile.setBio(limit(request.getParameter("bio"), 500));
        profile.setUniversity(limit(request.getParameter("university"), 255));
        profile.setMajor(limit(request.getParameter("major"), 255));
        profile.setPortfolioUrl(normalizeLink(request.getParameter("portfolioUrl")));
        profile.setLinkedinUrl(normalizeLink(request.getParameter("linkedinUrl")));
        profile.setTelegramUrl(normalizeLink(request.getParameter("telegramUrl")));

        byte[] avatar = null;
        try {
            Part avatarPart = request.getPart("avatar");
            if (avatarPart != null && avatarPart.getSize() > 0) {
                try (InputStream input = avatarPart.getInputStream()) {
                    avatar = AvatarCompressor.compressToJpeg(input, avatarPart.getSize());
                }
            }
        } catch (IllegalArgumentException e) {
            response.sendRedirect(request.getContextPath() + "/profile?error=photo");
            return;
        }

        if (profileDAO.updateProfile(profile, avatar)) {
            response.sendRedirect(request.getContextPath() + "/profile?saved=1");
        } else {
            response.sendRedirect(request.getContextPath() + "/profile?error=save");
        }
    }

    private void showPublicProfile(HttpServletRequest request, HttpServletResponse response, User signedInUser)
            throws ServletException, IOException {
        Integer profileId = parseId(request.getParameter("id"));
        if (profileId == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        Profile profile = profileDAO.getByUserId(profileId);
        if (profile == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        request.setAttribute("profile", profile);
        request.setAttribute("isOwnProfile", profileId == signedInUser.getId());
        request.getRequestDispatcher("/views/profile/view.jsp").forward(request, response);
    }

    private void serveAvatar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Integer profileId = parseId(request.getParameter("id"));
        if (profileId == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        ProfileDAO.Avatar avatar = profileDAO.getAvatar(profileId);
        if (avatar == null || avatar.getData() == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        response.setContentType(avatar.getContentType() == null ? "image/jpeg" : avatar.getContentType());
        response.setContentLength(avatar.getData().length);
        response.setHeader("Cache-Control", "private, max-age=86400");
        response.getOutputStream().write(avatar.getData());
    }

    private User getSignedInUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth/signin");
        }
        return user;
    }

    private Integer parseId(String value) {
        try {
            int id = Integer.parseInt(value);
            return id > 0 ? id : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String limit(String value, int maxLength) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized.substring(0, Math.min(normalized.length(), maxLength));
    }

    private String normalizeLink(String value) {
        String link = limit(value, 500);
        if (link == null) {
            return null;
        }
        return (link.startsWith("https://") || link.startsWith("http://")) ? link : null;
    }
}
