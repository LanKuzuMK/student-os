package com.studentos.controller;

import com.studentos.util.AppHealth;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/** Minimal unauthenticated liveness/readiness check for the hosting platform. */
@WebServlet(name = "healthController", urlPatterns = "/health")
public class HealthController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        boolean databaseAvailable = AppHealth.databaseAvailable();
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Cache-Control", "no-store, max-age=0");
        response.setStatus(databaseAvailable ? HttpServletResponse.SC_OK : HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        response.getWriter().write(databaseAvailable ? "{\"status\":\"ok\"}" : "{\"status\":\"unavailable\"}");
    }
}
