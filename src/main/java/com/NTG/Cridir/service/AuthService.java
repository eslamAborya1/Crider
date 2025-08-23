package com.NTG.Cridir.service;

import com.NTG.Cridir.DTOs.*;
import com.NTG.Cridir.mapper.UserMapper;
import com.NTG.Cridir.model.Customer;
import com.NTG.Cridir.model.Enum.Role;
import com.NTG.Cridir.model.Provider;
import com.NTG.Cridir.model.User;
import com.NTG.Cridir.repository.CustomerRepository;
import com.NTG.Cridir.repository.ProviderRepository;
import com.NTG.Cridir.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final ProviderRepository providerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final UserMapper userMapper; // ← ده اللي عملناه manual

    public AuthService(UserRepository userRepository,
                       CustomerRepository customerRepository,
                       ProviderRepository providerRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       EmailService emailService,
                       UserMapper userMapper) {
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.providerRepository = providerRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.userMapper = userMapper;
    }

    // ----------------- SIGNUP -----------------
    public AuthResponse signup(SignupRequest request) {
        validateSignup(request);

        // نستخدم UserMapper اللي كتبناه manual
        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        userRepository.save(user);

        createRoleEntity(user);

        String activationToken = jwtService.generateToken(user);
        emailService.sendActivationEmail(user, activationToken);

        return new AuthResponse(user.getUserId(), user.getEmail(), user.getRole(), activationToken);
    }

    // ----------------- LOGIN -----------------
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPassword()))
            throw new RuntimeException("Invalid email or password");

        if (!user.isEnabled())
            throw new RuntimeException("Account not activated. Please check your email.");

        String token = jwtService.generateToken(user);
        return new AuthResponse(user.getUserId(), user.getEmail(), user.getRole(), token);
    }

    // ----------------- ACTIVATE -----------------
    public void activateAccount(String token) {
        String email = jwtService.extractEmail(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid token"));
        user.setEnabled(true);
        userRepository.save(user);
    }

    // ----------------- RESET PASSWORD -----------------
    public void resetPassword(ResetPasswordRequest request) {
        String email = jwtService.extractEmail(request.token());
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (passwordEncoder.matches(request.newPassword(), user.getPassword()))
            throw new RuntimeException("New password cannot be the same as the old password");

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    // ----------------- Helpers -----------------
    private void validateSignup(SignupRequest request) {
        if (userRepository.existsByEmail(request.email()))
            throw new RuntimeException("Email already in use");
    }

    private void createRoleEntity(User user) {
        if (user.getRole() == Role.CUSTOMER) {
            var c = new Customer(); c.setUser(user); customerRepository.save(c);
        } else if (user.getRole() == Role.PROVIDER) {
            var p = new Provider(); p.setUser(user); providerRepository.save(p);
        }
    }

    public Long getCurrentUserId(String token) {
        String email = jwtService.extractEmail(token);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getUserId();
    }
}
