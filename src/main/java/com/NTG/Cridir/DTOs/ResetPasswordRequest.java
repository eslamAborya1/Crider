package com.NTG.Cridir.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

public record ResetPasswordRequest(
        String newPassword
) {}

