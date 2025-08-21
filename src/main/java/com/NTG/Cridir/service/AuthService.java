package com.NTG.Cridir.service;

import com.NTG.Cridir.DTOs.*;
import com.NTG.Cridir.model.Customer;
import com.NTG.Cridir.model.Provider;
import com.NTG.Cridir.model.User;
import com.NTG.Cridir.model.Enum.Role;
import com.NTG.Cridir.repository.CustomerRepository;
import com.NTG.Cridir.repository.ProviderRepository;
import com.NTG.Cridir.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final ProviderRepository providerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Autowired
    public AuthService(UserRepository userRepository,
                       CustomerRepository customerRepository,
                       ProviderRepository providerRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.providerRepository = providerRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse signup(SignupRequest request) {
        // email uniqueness
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already in use");
        }
        // password checks
        if (request.password().length() < 6
                || !request.password().matches(".*\\d.*")
                || !request.password().matches(".*[a-zA-Z].*")) {
            throw new RuntimeException("Password must be at least 6 characters and contain letters and numbers");
        }
        // Egyptian phone format
        if (!request.phone().matches("^(010|011|012|015)[0-9]{8}$")) {
            throw new RuntimeException("Phone number must be 11 digits and start with 010/011/012/015");
        }

        // Create base user
        User user = new User();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(request.role());
        user.setName(request.name());
        user.setPhone(request.phone());

        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            // covers unique(phone) as well
            throw new RuntimeException("Email or phone already in use");
        }

        // Create role entity
        if (request.role() == Role.CUSTOMER) {
            Customer c = new Customer();
            c.setUser(user);
            customerRepository.save(c);
        } else if (request.role() == Role.PROVIDER) {
            Provider p = new Provider();
            p.setUser(user);
            providerRepository.save(p);
        } else {
            throw new RuntimeException("Invalid role provided");
        }

        String token = jwtService.generateToken(user);
        return new AuthResponse(user.getUserId(), user.getEmail(), user.getRole(), token);
    }

    public AuthResponse login(LoginRequest request) {
        if (request.password() == null || request.password().isBlank()) {
            throw new RuntimeException("Password cannot be empty");
        }

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtService.generateToken(user);
        return new AuthResponse(user.getUserId(), user.getEmail(), user.getRole(), token);
    }

    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (passwordEncoder.matches(request.newPassword(), user.getPassword())) {
            throw new RuntimeException("New password cannot be the same as the old password");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }
}
