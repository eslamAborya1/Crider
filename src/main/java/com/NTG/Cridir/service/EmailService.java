package com.NTG.Cridir.service;

import com.NTG.Cridir.events.EmailEvent;
import com.NTG.Cridir.events.EmailProducer;
import com.NTG.Cridir.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final EmailProducer producer;

    @Value("${app.frontend.base-url}")
    private String frontendBaseUrl;

    public EmailService(EmailProducer producer) {
        this.producer = producer;
    }

    public void sendEmail(String to, String subject, String htmlContent) {
        producer.sendEmailEvent(new EmailEvent(to, subject, htmlContent));
    }

    public void sendActivationEmail(User user, String token) {
        String link = frontendBaseUrl + "/activate?token=" + token;
        String html = """
            <p>Hi %s,</p>
            <p>Please activate your account by clicking the link below:</p>
            <a href="%s">Activate Account</a>
        """.formatted(user.getName(), link);
        sendEmail(user.getEmail(), "Activate your Cridir account", html);
    }

    public void sendResetPasswordEmail(User user, String token) {
        String link = frontendBaseUrl + "/reset-password?token=" + token;
        String html = """
            <p>Hi %s,</p>
            <p>Click the link to reset your password:</p>
            <a href="%s">Reset Password</a>
        """.formatted(user.getName(), link);
        sendEmail(user.getEmail(), "Reset your Cridir password", html);
    }
}
