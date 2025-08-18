package com.NTG.Cridir.DTOs;


import com.NTG.Cridir.model.Enum.Role;

public record AuthResponse(
        Long userId,
        String email,
        Role role,
        String token
) { }