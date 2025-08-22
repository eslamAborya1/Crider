package com.NTG.Cridir.mapper;

import com.NTG.Cridir.DTOs.SignupRequest;
import com.NTG.Cridir.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")

public interface UserMapper {
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "enabled", constant = "false")
    @Mapping(target = "password", ignore = true) // هنحطها يدوي بعد التشفير
    User toEntity(SignupRequest request);
}
