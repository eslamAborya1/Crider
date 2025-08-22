
package com.NTG.Cridir.DTOs;

import com.NTG.Cridir.model.Enum.Role;
import jakarta.validation.constraints.*;
public record SignupRequest(

        @NotBlank
        @Email(message = "Email format is invalid")
        String email,

        @NotBlank
        @Size(min = 6, max = 100, message = "Password must be at least 6 characters")
        @Pattern(regexp = ".*\\d.*", message = "Password must contain at least one number")
        @Pattern(regexp = ".*[a-zA-Z].*", message = "Password must contain at least one letter")
        String password,

        @NotBlank
        String name,

        @NotBlank
        @Pattern(
                regexp = "^(010|011|012|015)[0-9]{8}$",
                message = "Phone number must be 11 digits and start with 010/011/012/015"
        )
        String phone,

        @NotNull
        Role role // CUSTOMER or PROVIDER
) { }
