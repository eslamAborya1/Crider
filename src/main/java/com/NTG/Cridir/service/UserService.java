package com.NTG.Cridir.service;

import com.NTG.Cridir.DTOs.UserProfileDTO;
import com.NTG.Cridir.DTOs.UserUpdateRequest;
import com.NTG.Cridir.exception.NotFoundException;
import com.NTG.Cridir.mapper.UserMapper;
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
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository,
                       CustomerRepository customerRepository,
                       ProviderRepository providerRepository,
                       UserMapper userMapper) {
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.providerRepository = providerRepository;
        this.userMapper = userMapper;
    }

    public UserProfileDTO getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return userMapper.toProfileDTO(user);
    }
    @Transactional
    public UserProfileDTO updateProfile(String email, UserUpdateRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        userMapper.updateUserFromRequest(request, user);
        userRepository.save(user);

        return userMapper.toProfileDTO(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        switch (user.getRole()) {
            case CUSTOMER -> customerRepository.deleteByUserUserId(userId);
            case PROVIDER -> providerRepository.deleteByUserUserId(userId);
            default -> userRepository.delete(user);
        }
    }
}
