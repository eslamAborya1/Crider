package com.NTG.Cridir.controller;

import com.NTG.Cridir.DTOs.AvailabilityResponse;
import com.NTG.Cridir.DTOs.ProviderAvailabilityRequest;
import com.NTG.Cridir.DTOs.ProviderResponseDTO;
import com.NTG.Cridir.model.Provider;
import com.NTG.Cridir.repository.ProviderRepository;
import com.NTG.Cridir.service.ProviderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/providers")
public class ProviderController {
    private final ProviderRepository providerRepository;
    private final ProviderService providerService;


    public ProviderController(ProviderRepository providerRepository, ProviderService providerService) {
        this.providerRepository = providerRepository;

        this.providerService = providerService;
    }
    @PreAuthorize("hasRole('PROVIDER')")
    @PutMapping("/{providerId}/availability")
    public ResponseEntity<AvailabilityResponse> toggleAvailability(
            @PathVariable Long providerId,
            @RequestBody @Valid ProviderAvailabilityRequest req) {

        providerService.toggleProviderAvailability(providerId, req.available());

        AvailabilityResponse response = new AvailabilityResponse(
                providerId,
                req.available(),
                "Availability updated successfully"
        );

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/available")
    public List<ProviderResponseDTO> availableProviders() {
        return providerService.getAvailableProviders();
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/{providerId}")
    public ProviderResponseDTO getProvider(@PathVariable Long providerId) {
        return providerService.getProviderById(providerId);
    }
}
