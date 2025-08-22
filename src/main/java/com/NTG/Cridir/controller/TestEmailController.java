package com.NTG.Cridir.controller;

import com.NTG.Cridir.service.EmailService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestEmailController {
    private final EmailService emailService;

    public TestEmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/email")
    public String sendTestEmail() {
        emailService.sendEmail(
                "test@example.com",
                "Hello from Cridir",
                "<h1>It works with MailTrap!</h1>"
        );
        return "Email sent!";
    }
}

