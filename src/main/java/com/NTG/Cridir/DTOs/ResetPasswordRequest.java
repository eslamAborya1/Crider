package com.NTG.Cridir.DTOs;


import jakarta.validation.constraints.*;

public record ResetPasswordRequest(
        @NotBlank @Email String email,
        @NotBlank String newPassword
) { }