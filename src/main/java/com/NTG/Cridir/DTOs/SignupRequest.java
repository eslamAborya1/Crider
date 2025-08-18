
package com.NTG.Cridir.DTOs;

import com.NTG.Cridir.model.Enum.Role;
import jakarta.validation.constraints.*;

public record SignupRequest(
        @NotBlank @Email
        String email,
        @NotBlank @Size(min = 6, max = 100)
        String password,
        @NotBlank
        String name,
        @NotBlank
        String phone,
        @NotNull
        Role role // CUSTOMER or PROVIDER
) { }
