package com.NTG.Cridir.service;

import com.NTG.Cridir.model.Customer;
import com.NTG.Cridir.model.Enum.Role;
import com.NTG.Cridir.model.Provider;
import com.NTG.Cridir.model.User;
import com.NTG.Cridir.repository.CustomerRepository;
import com.NTG.Cridir.repository.ProviderRepository;
import com.NTG.Cridir.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final ProviderRepository providerRepository;

    public UserService(UserRepository userRepository, CustomerRepository customerRepository, ProviderRepository providerRepository) {
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.providerRepository = providerRepository;
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() == Role.CUSTOMER) {
            Customer customer = customerRepository.findAll()
                    .stream()
                    .filter(c -> c.getUser().getUserId().equals(userId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Customer not found"));
            customerRepository.delete(customer);
        } else if (user.getRole() == Role.PROVIDER) {
            Provider provider = providerRepository.findAll()
                    .stream()
                    .filter(p -> p.getUser().getUserId().equals(userId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Provider not found"));
            providerRepository.delete(provider);
        } else {
            // fallback: just delete user
            userRepository.delete(user);
        }
    }





}
