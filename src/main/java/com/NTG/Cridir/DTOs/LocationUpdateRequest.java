package com.NTG.Cridir.DTOs;

import jakarta.validation.constraints.NotNull;

public record LocationUpdateRequest(
        @NotNull Long providerId,
        @NotNull Double latitude,
        @NotNull Double longitude
) {}
