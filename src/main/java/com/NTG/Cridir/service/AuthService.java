package com.NTG.Cridir.service;

import com.NTG.Cridir.model.User;
import com.NTG.Cridir.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

//    public User signUp (String email, String password, String name, String phone , User.Role role){
//        if(userRepository.findByEmail(email).isPresent()){
//            throw new RuntimeException("Email Already exist");
//        }
//
//    }
}
