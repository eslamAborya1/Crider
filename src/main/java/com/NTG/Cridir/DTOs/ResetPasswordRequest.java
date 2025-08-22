package com.NTG.Cridir.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

public record ResetPasswordRequest(
        @NotBlank String token,
        @NotBlank
        @Size(min = 6, max = 100, message = "Password must be at least 6 characters")
        @Pattern(regexp = ".*\\d.*", message = "Password must contain at least one number")
        @Pattern(regexp = ".*[a-zA-Z].*", message = "Password must contain at least one letter")
        String newPassword
) {}
