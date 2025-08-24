package com.NTG.Cridir.DTOs;

public record ProviderResponseDTO(
        Long providerId,
        String name,
        String phone,
        boolean availabilityStatus,
        Double latitude,
        Double longitude
) {}
