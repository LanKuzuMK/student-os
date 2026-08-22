package com.studentos.util;

import com.studentos.dao.AuthSessionDAO;
import com.studentos.dao.UserDAO;
import com.studentos.model.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;

/** Restores secure, revocable browser login state after a normal application restart. */
public final class PersistentSessionManager {
    private static final String COOKIE_NAME = "STUDENTOS_AUTH";
    private static final Duration INACTIVITY_WINDOW = Duration.ofDays(7);
    private static final int MAX_AGE_SECONDS = (int) INACTIVITY_WINDOW.toSeconds();
    private static final SecureRandom RANDOM = new SecureRandom();

    private final AuthSessionDAO authSessionDAO = new AuthSessionDAO();
    private final UserDAO userDAO = new UserDAO();

    public void establish(HttpServletRequest request, HttpServletResponse response, User user) {
        request.getSession();
        request.changeSessionId();
        HttpSession session = request.getSession();
        session.setAttribute("user", user);
        session.setAttribute("authVersion", user.getAuthVersion());

        String token = generateToken();
        authSessionDAO.create(user.getId(), user.getAuthVersion(), hashToken(token), Instant.now().plus(INACTIVITY_WINDOW));
        writeCookie(request, response, token, MAX_AGE_SECONDS);
    }

    public User restore(HttpServletRequest request, HttpServletResponse response) {
        String token = readToken(request);
        if (token == null) {
            return null;
        }

        String tokenHash = hashToken(token);
        AuthSessionDAO.SessionIdentity identity = authSessionDAO.findActive(tokenHash);
        if (identity == null) {
            clearCookie(request, response);
            return null;
        }

        User user = userDAO.findById(identity.userId());
        if (user == null || !"ACTIVE".equals(user.getStatus())
                || !SessionVersionUtil.isCurrent(identity.authVersion(), user.getAuthVersion())) {
            authSessionDAO.revoke(tokenHash);
            clearCookie(request, response);
            return null;
        }

        HttpSession session = request.getSession();
        session.setAttribute("user", user);
        session.setAttribute("authVersion", user.getAuthVersion());
        refresh(request, response, tokenHash);
        return user;
    }

    public void refreshIfPresent(HttpServletRequest request, HttpServletResponse response) {
        String token = readToken(request);
        if (token != null) {
            refresh(request, response, hashToken(token));
        }
    }

    public void revokeCurrent(HttpServletRequest request, HttpServletResponse response) {
        String token = readToken(request);
        if (token != null) {
            authSessionDAO.revoke(hashToken(token));
        }
        clearCookie(request, response);
    }

    public void revokeAllForUser(int userId) {
        authSessionDAO.revokeAllForUser(userId);
    }

    static String generateToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    static String hashToken(String token) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("Could not protect an authenticated session", e);
        }
    }

    static int inactivityWindowSeconds() {
        return MAX_AGE_SECONDS;
    }

    private String readToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (COOKIE_NAME.equals(cookie.getName()) && cookie.getValue() != null && !cookie.getValue().isBlank()) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private void clearCookie(HttpServletRequest request, HttpServletResponse response) {
        writeCookie(request, response, "", 0);
    }

    private void refresh(HttpServletRequest request, HttpServletResponse response, String tokenHash) {
        if (authSessionDAO.extend(tokenHash, Instant.now().plus(INACTIVITY_WINDOW))) {
            String token = readToken(request);
            if (token != null) {
                writeCookie(request, response, token, MAX_AGE_SECONDS);
            }
        }
    }

    private void writeCookie(HttpServletRequest request, HttpServletResponse response, String value, int maxAge) {
        String contextPath = request.getContextPath();
        String path = contextPath == null || contextPath.isBlank() ? "/" : contextPath;
        response.addHeader("Set-Cookie", COOKIE_NAME + "=" + value + "; Path=" + path + "; Max-Age=" + maxAge
                + "; HttpOnly; Secure; SameSite=Lax");
    }
}
