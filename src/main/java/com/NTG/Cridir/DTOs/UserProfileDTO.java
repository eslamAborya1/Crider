package com.NTG.Cridir.DTOs;

import com.NTG.Cridir.model.Enum.Role;

public record UserProfileDTO(
        Long userId,
        String email,
        Role role,
        String name,
        String phone
) { }