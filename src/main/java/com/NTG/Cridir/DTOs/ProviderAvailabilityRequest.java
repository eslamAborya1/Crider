package com.NTG.Cridir.DTOs;

import jakarta.validation.constraints.NotNull;

public record ProviderAvailabilityRequest(
        @NotNull Boolean available
) {}
