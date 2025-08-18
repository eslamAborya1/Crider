package com.NTG.Cridir.DTOs;

import jakarta.validation.constraints.*;

public record LoginRequest(
        @NotBlank @Email
        String email,
        @NotBlank
        String password
) { }
