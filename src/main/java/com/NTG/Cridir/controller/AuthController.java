package com.NTG.Cridir.controller;

import com.NTG.Cridir.DTOs.*;
import com.NTG.Cridir.service.AuthService;
import com.NTG.Cridir.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final EmailService emailService;
    public AuthController(AuthService authService, EmailService emailService) { this.authService = authService;
        this.emailService = emailService;
    }

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest request) {
        return ResponseEntity.ok(authService.signup(request));
    }


    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/activate")
    public ResponseEntity<String> activate(@RequestParam String token) {
        authService.activateAccount(token);
        return ResponseEntity.ok("Account activated successfully!");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        emailService.sendResetPasswordCode(request.email());
        return ResponseEntity.ok("Verification code sent to your email!");
    }

    @PostMapping("/verify-reset-code")
    public ResponseEntity<String> verifyResetCode(
            @RequestParam String email,
            @RequestParam String code) {
        authService.verifyResetCode(email, code);
        return ResponseEntity.ok("Code verified. You can reset your password now.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok("Password reset successful!");
    }

}
