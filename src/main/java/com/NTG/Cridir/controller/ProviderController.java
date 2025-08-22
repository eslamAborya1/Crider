package com.NTG.Cridir.controller;

import com.NTG.Cridir.DTOs.ProviderAvailabilityRequest;
import com.NTG.Cridir.model.Provider;
import com.NTG.Cridir.repository.ProviderRepository;
import com.NTG.Cridir.service.LocationService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/providers")
public class ProviderController {
    private final ProviderRepository providerRepository;
    private final LocationService locationService;

    public ProviderController(ProviderRepository providerRepository, LocationService locationService) {
        this.providerRepository = providerRepository;
        this.locationService = locationService;
    }
    @PreAuthorize("hasRole('PROVIDER')")
    @PutMapping("/{providerId}/availability")
    public void toggleAvailability(@PathVariable Long providerId,
                                   @RequestBody @Valid ProviderAvailabilityRequest req) {
        locationService.toggleProviderAvailability(providerId, req.available());
    }
    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/available")
    public List<Provider> availableProviders() {
        return providerRepository.findByAvailabilityStatusTrue();
    }
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('PROVIDER')")
    @GetMapping("/{providerId}")
    public Provider getProvider(@PathVariable Long providerId) {
        return providerRepository.findById(providerId)
                .orElseThrow(() -> new EntityNotFoundException("Provider not found"));
    }
}
