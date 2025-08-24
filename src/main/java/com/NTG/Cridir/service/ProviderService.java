package com.NTG.Cridir.service;

import com.NTG.Cridir.DTOs.ProviderResponseDTO;
import com.NTG.Cridir.exception.NotFoundException;
import com.NTG.Cridir.mapper.ProviderMapper;
import com.NTG.Cridir.model.Provider;
import com.NTG.Cridir.repository.ProviderRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ProviderService {
    private final ProviderRepository providerRepository;
    private final ProviderMapper providerMapper;

    public ProviderService(ProviderRepository providerRepository, ProviderMapper providerMapper) {
        this.providerRepository = providerRepository;
        this.providerMapper = providerMapper;
    }

    public void toggleProviderAvailability(Long providerId, boolean available) {
        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new NotFoundException("Provider not found"));

        provider.setAvailabilityStatus(available);
        providerRepository.save(provider);
    }

    public List<ProviderResponseDTO> getAvailableProviders() {
        return providerRepository.findByAvailabilityStatusTrue()
                .stream()
                .map(providerMapper::toResponseDTO)
                .toList();
    }

    public ProviderResponseDTO getProviderById(Long providerId) {
        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new EntityNotFoundException("Provider not found"));
        return providerMapper.toResponseDTO(provider);
    }
}
