package com.NTG.Cridir.service;
import com.NTG.Cridir.DTOs.UserProfileDTO;
import com.NTG.Cridir.DTOs.UserUpdateRequest;
import com.NTG.Cridir.model.User;
import com.NTG.Cridir.repository.CustomerRepository;
import com.NTG.Cridir.repository.ProviderRepository;
import com.NTG.Cridir.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
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
    public UserProfileDTO getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return mapToResponse(user);
    }

    // ✅ Update profile
    public UserProfileDTO updateProfile(String email, UserUpdateRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (request.name() != null) user.setName(request.name());
        if (request.phone() != null) user.setPhone(request.phone());

        userRepository.save(user);
        return mapToResponse(user);
    }

    private UserProfileDTO mapToResponse(User user) {
        return new UserProfileDTO(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                user.getName(),
                user.getPhone()
        );
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