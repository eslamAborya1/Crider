package com.NTG.Cridir.service;

import com.NTG.Cridir.model.User;
import com.NTG.Cridir.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserByEmail (String email){
       return
               userRepository.findByEmail(email)
               .orElseThrow(()-> new RuntimeException("User with email"+email+"doesn't exist"));

    }

    public User getUserById(Long id){
        return userRepository.findByUserId(id)
                .orElseThrow(()-> new RuntimeException("User with id "+id+" doesn't exist"));
    }


}
