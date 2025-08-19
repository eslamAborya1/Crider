package com.NTG.Cridir.DTOs;

import jakarta.validation.constraints.*;

public record LoginRequest(
        @NotBlank @Email(message = "Email format is invalid")
        String email,
        @NotBlank
        String password
) { }
