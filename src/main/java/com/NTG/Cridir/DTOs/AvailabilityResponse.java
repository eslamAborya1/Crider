package com.NTG.Cridir.DTOs;

public record AvailabilityResponse(
        Long providerId,
        boolean availability,
        String message
) {
}
