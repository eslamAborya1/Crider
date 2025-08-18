package com.NTG.Cridir.DTOs;

public record UserProfileDTO(
        Long userId,
        String email,
        String role,
        String name,
        String phone
) { }