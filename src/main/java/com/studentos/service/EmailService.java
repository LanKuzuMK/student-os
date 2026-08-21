package com.studentos.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/** Sends transactional verification messages through Brevo's HTTPS API. */
public class EmailService {
    private static final URI BREVO_EMAILS_URI = URI.create("https://api.brevo.com/v3/smtp/email");
    private final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();

    public boolean isConfigured() {
        return readEnvironment("BREVO_API_KEY") != null && readEnvironment("EMAIL_FROM") != null;
    }

    public boolean sendVerificationCode(String recipient, String code) {
        String apiKey = readEnvironment("BREVO_API_KEY");
        String sender = readEnvironment("EMAIL_FROM");
        if (apiKey == null || sender == null) {
            return false;
        }

        JsonObject payload = new JsonObject();
        JsonObject senderDetails = new JsonObject();
        senderDetails.addProperty("name", "StudentOS");
        senderDetails.addProperty("email", sender);
        payload.add("sender", senderDetails);

        JsonArray recipients = new JsonArray();
        JsonObject recipientDetails = new JsonObject();
        recipientDetails.addProperty("email", recipient);
        recipients.add(recipientDetails);
        payload.add("to", recipients);
        payload.addProperty("subject", "Your StudentOS verification code");
        payload.addProperty("textContent", "Your StudentOS verification code is " + code + ". It expires in 10 minutes. If you did not request this, you can ignore this email.");
        payload.addProperty("htmlContent", "<p>Your StudentOS verification code is:</p><p style=\"font-size:24px;font-weight:700;letter-spacing:0.16em\">" + code + "</p><p>This code expires in 10 minutes. If you did not request it, you can ignore this email.</p>");

        HttpRequest request = HttpRequest.newBuilder(BREVO_EMAILS_URI)
                .timeout(Duration.ofSeconds(20))
                .header("api-key", apiKey)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return true;
            }
            System.err.println("Verification email delivery failed: Brevo HTTP " + response.statusCode());
        } catch (Exception exception) {
            System.err.println("Verification email delivery failed: " + exception.getClass().getSimpleName()
                    + " - " + exception.getMessage());
        }
        return false;
    }

    private String readEnvironment(String name) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? null : value.trim();
    }
}
