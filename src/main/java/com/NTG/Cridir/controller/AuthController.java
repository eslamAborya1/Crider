package com.NTG.Cridir.controller;

import com.NTG.Cridir.model.User;
import com.NTG.Cridir.repository.UserRepository;
import com.NTG.Cridir.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {


    private final UserRepository userRepository;

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;
    @Autowired
    public AuthController(UserRepository userRepository, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }


    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already taken!");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        Optional<User> dbUser = userRepository.findByEmail(user.getEmail());
        if (dbUser.isEmpty() || !passwordEncoder.matches(user.getPassword(), dbUser.get().getPassword())) {
            return ResponseEntity.badRequest().body("Invalid credentials");
        }

        String token = jwtService.generateToken(dbUser.get());
        return ResponseEntity.ok(token);
    }


    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String email, @RequestParam String newPassword) {
        Optional<User> dbUser = userRepository.findByEmail(email);
        if (dbUser.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }
        dbUser.get().setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(dbUser.get());
        return ResponseEntity.ok("Password reset successful");
    }
}
