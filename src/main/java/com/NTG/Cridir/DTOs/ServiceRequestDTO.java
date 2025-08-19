// DTOs/ServiceRequestDTO.java
package com.NTG.Cridir.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

//Used when client sends data to us (backend) (e.g. Customer creating a request).

public record ServiceRequestDTO(
        @NotNull Long customerId,
        @NotBlank String issueType,
        @NotBlank String carType,
        @NotNull Double latitude,
        @NotNull Double longitude
) {}
