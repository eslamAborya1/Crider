package com.NTG.Cridir.controller;

import com.NTG.Cridir.DTOs.AuthResponse;
import com.NTG.Cridir.DTOs.LoginRequest;
import com.NTG.Cridir.DTOs.ResetPasswordRequest;
import com.NTG.Cridir.DTOs.SignupRequest;
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
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest request) {
        return ResponseEntity.ok(authService.signup(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // GET /auth/activate?token=...
    @GetMapping("/activate")
    public ResponseEntity<String> activate(@RequestParam String token) {
        authService.activateAccount(token);
        return ResponseEntity.ok("Account activated successfully!");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        emailService.sendResetPasswordEmail(email);
        return ResponseEntity.ok("Reset password link sent!");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok("Password reset successful!");
    }
}
