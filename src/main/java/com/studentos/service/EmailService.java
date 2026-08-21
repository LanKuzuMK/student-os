package com.studentos.service;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

/** Sends transactional verification messages through Gmail SMTP and a dedicated App Password. */
public class EmailService {
    public boolean isConfigured() {
        return readEnvironment("GMAIL_SMTP_USERNAME") != null
                && readEnvironment("GMAIL_SMTP_APP_PASSWORD") != null
                && readEnvironment("EMAIL_FROM") != null;
    }

    public boolean sendVerificationCode(String recipient, String code) {
        String username = readEnvironment("GMAIL_SMTP_USERNAME");
        String appPassword = readEnvironment("GMAIL_SMTP_APP_PASSWORD");
        String sender = readEnvironment("EMAIL_FROM");
        if (username == null || appPassword == null || sender == null) {
            return false;
        }

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.connectiontimeout", "10000");
        properties.put("mail.smtp.timeout", "15000");
        properties.put("mail.smtp.writetimeout", "15000");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, appPassword);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(sender, "StudentOS", StandardCharsets.UTF_8.name()));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient, false));
            message.setSubject("Your StudentOS verification code", StandardCharsets.UTF_8.name());
            message.setText("Your StudentOS verification code is " + code + ". It expires in 10 minutes. If you did not request this, you can ignore this email.", StandardCharsets.UTF_8.name());
            Transport.send(message);
            return true;
        } catch (Exception exception) {
            System.err.println("Verification email delivery failed: " + exception.getClass().getSimpleName()
                    + " - " + exception.getMessage());
            return false;
        }
    }

    private String readEnvironment(String name) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? null : value.trim();
    }
}
