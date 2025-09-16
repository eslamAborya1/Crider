package com.NTG.Cridir.mapper;

import com.NTG.Cridir.DTOs.SignupRequest;
import com.NTG.Cridir.DTOs.UserProfileDTO;
import com.NTG.Cridir.DTOs.UserUpdateRequest;
import com.NTG.Cridir.model.Enum.Role;
import com.NTG.Cridir.model.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "enabled", constant = "false")
    @Mapping(target = "password", ignore = true)
    User toEntity(SignupRequest request);

//    @Mapping(source = "email", target = "email")
//    @Mapping(source = "role", target = "role")
//    @Mapping(source = "name", target = "name")
//    @Mapping(source = "phone", target = "phone")
//    UserProfileDTO toProfileDTO(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromRequest(UserUpdateRequest request, @MappingTarget User user);


    default UserProfileDTO toProfileDTO(User user) {
        Long finalId;
        if (user.getRole() == Role.PROVIDER && user.getProvider() != null) {
            finalId = user.getProvider().getProviderId();
        } else if (user.getRole() == Role.CUSTOMER && user.getCustomer() != null) {
            finalId = user.getCustomer().getCustomerId();
        } else {
            finalId = user.getUserId(); // fallback
        }

        return new UserProfileDTO(
                finalId,
                user.getEmail(),
                user.getRole(),
                user.getName(),
                user.getPhone()
        );
    }

}
