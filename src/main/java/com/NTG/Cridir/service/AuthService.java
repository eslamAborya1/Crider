package com.NTG.Cridir.service;

import com.NTG.Cridir.DTOs.*;
import com.NTG.Cridir.model.Customer;
import com.NTG.Cridir.model.Provider;
import com.NTG.Cridir.model.User;
import com.NTG.Cridir.model.Enum.Role;
import com.NTG.Cridir.repository.CustomerRepository;
import com.NTG.Cridir.repository.ProviderRepository;
import com.NTG.Cridir.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already in use");
        }

        User user = new User();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(request.role());

        user = userRepository.save(user);

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
        }

        String token = jwtService.generateToken(user);

        return new AuthResponse(user.getUserId(), user.getEmail(), user.getRole(), token);
    }


    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtService.generateToken(user);
        return new AuthResponse(user.getUserId(), user.getEmail(), user.getRole(), token);
    }


    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }
}
