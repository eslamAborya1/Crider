// DTOs/ServiceRequestDTO.java
package com.NTG.Cridir.DTOs;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

//Used when client sends data to us (backend) (e.g. Customer creating a request).

public record ServiceRequestDTO(
        @NotNull Long customerId,
        @NotBlank String issueType,
        @NotBlank String carType,
        @NotNull(message = "Car model year is required")
        @Min(value = 1990, message = "Car model year must be after 1980")
        Integer carModelYear,
        @NotNull Double latitude,
        @NotNull Double longitude
) {}
