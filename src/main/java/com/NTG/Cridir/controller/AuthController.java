package com.NTG.Cridir.controller;

import com.NTG.Cridir.DTOs.AuthResponse;
import com.NTG.Cridir.DTOs.LoginRequest;
import com.NTG.Cridir.DTOs.ResetPasswordRequest;
import com.NTG.Cridir.DTOs.SignupRequest;
import com.NTG.Cridir.repository.UserRepository;
import com.NTG.Cridir.service.AuthService;
import com.NTG.Cridir.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;

    }


        @PostMapping("/signup")
        public ResponseEntity<AuthResponse> signup(@RequestBody SignupRequest request) {
            AuthResponse response = authService.signup(request);
            return ResponseEntity.ok(response);
        }

        @PostMapping("/login")
        public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            authService.resetPassword(request);
            return ResponseEntity.ok("Password reset successful");
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

}
