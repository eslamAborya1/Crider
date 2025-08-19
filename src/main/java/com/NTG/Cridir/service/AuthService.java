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
        // Check email uniqueness
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already in use");
        }

        // Validate password strength
        if (request.password().length() < 6 || !request.password().matches(".*\\d.*") || !request.password().matches(".*[a-zA-Z].*")) {
            throw new RuntimeException("Password must be at least 6 characters and contain both letters and numbers");
        }

        // Validate Egyptian phone number (must be 11 digits and start with 010, 011, 012, or 015)
        if (!request.phone().matches("^(010|011|012|015)[0-9]{8}$")) {
            throw new RuntimeException("Phone number must be 11 digits and start with 01 ");
        }

        // Check phone uniqueness for customer or provider
        if (request.role() == Role.CUSTOMER && customerRepository.findAll()
                .stream().anyMatch(c -> c.getPhone().equals(request.phone()))) {
            throw new RuntimeException("Phone number already in use by another customer");
        }
        if (request.role() == Role.PROVIDER && providerRepository.findAll()
                .stream().anyMatch(p -> p.getPhone().equals(request.phone()))) {
            throw new RuntimeException("Phone number already in use by another provider");
        }

        // Create and save user
        User user = new User();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(request.role());

        user = userRepository.save(user);

        // 6. Save role-specific details
        if (request.role() == Role.CUSTOMER) {
            Customer customer = new Customer();
            customer.setUser(user);
            customer.setName(request.name());
            customer.setPhone(request.phone());
            customerRepository.save(customer);
        } else if (request.role() == Role.PROVIDER) {
            Provider provider = new Provider();
            provider.setUser(user);
            provider.setName(request.name());
            provider.setPhone(request.phone());
            providerRepository.save(provider);
        } else {
            throw new RuntimeException("Invalid role provided");
        }

        // 7. Generate token
        String token = jwtService.generateToken(user);

        // 8. Return AuthResponse
        return new AuthResponse(user.getUserId(), user.getEmail(), user.getRole(), token);
    }



    public AuthResponse login(LoginRequest request) {
        // Validate password is not blank
        if (request.password() == null || request.password().trim().isEmpty()) {
            throw new RuntimeException("Password cannot be empty");
        }

        // Find user by email
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        // Match password
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        // Generate JWT token
        String token = jwtService.generateToken(user);

        // Return response
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
