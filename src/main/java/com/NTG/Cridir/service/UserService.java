package com.NTG.Cridir.service;
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

        switch (user.getRole()) {
            case CUSTOMER -> customerRepository.deleteByUserUserId(userId);
            case PROVIDER -> providerRepository.deleteByUserUserId(userId);
            default -> userRepository.delete(user);
        }
    }

}