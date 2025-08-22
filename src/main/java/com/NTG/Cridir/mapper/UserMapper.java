package com.NTG.Cridir.mapper;

import com.NTG.Cridir.DTOs.SignupRequest;
import com.NTG.Cridir.DTOs.UserProfileDTO;
import com.NTG.Cridir.DTOs.UserUpdateRequest;
import com.NTG.Cridir.model.User;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface UserMapper {

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "enabled", constant = "false")
    @Mapping(target = "password", ignore = true) // هيتعمل encode بعدين
    User toEntity(SignupRequest request);

    // mapping User → UserProfileDTO
    @Mapping(source = "userId", target = "userId")  // Explicit mapping
    @Mapping(source = "email", target = "email")
    @Mapping(source = "role", target = "role")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "phone", target = "phone")
    UserProfileDTO toProfileDTO(User user);

    // update profile
    void updateUserFromRequest(UserUpdateRequest request, @MappingTarget User user);
}