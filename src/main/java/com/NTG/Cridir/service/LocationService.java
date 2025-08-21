package com.NTG.Cridir.service;

import com.NTG.Cridir.DTOs.LocationUpdateRequest;
import com.NTG.Cridir.Websocket.ProviderLocationSocketHandler;
import com.NTG.Cridir.model.Location;
import com.NTG.Cridir.model.Provider;
import com.NTG.Cridir.repository.LocationRepository;
import com.NTG.Cridir.repository.ProviderRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LocationService {

    private final LocationRepository locationRepository;
    private final ProviderRepository providerRepository;
    private final ProviderLocationSocketHandler socketHandler;

    public LocationService(LocationRepository locationRepository,
                           ProviderRepository providerRepository,
                           ProviderLocationSocketHandler socketHandler) {
        this.locationRepository = locationRepository;
        this.providerRepository = providerRepository;
        this.socketHandler = socketHandler;
    }

    @Transactional
    public void updateProviderLocation(LocationUpdateRequest req) {
        Provider provider = providerRepository.findById(req.providerId())
                .orElseThrow(() -> new EntityNotFoundException("Provider not found"));

        Location loc = provider.getCurrentLocation();
        if (loc == null) {
            loc = new Location();
        }
        loc.setLatitude(req.latitude());
        loc.setLongitude(req.longitude());
        loc = locationRepository.save(loc);

        provider.setCurrentLocation(loc);
        providerRepository.save(provider);

        // notify listeners
        socketHandler.broadcastProviderLocation(provider.getProviderId(), loc);
    }

    @Transactional(readOnly = true)
    public Location getProviderLocation(Long providerId) {
        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new EntityNotFoundException("Provider not found"));
        if (provider.getCurrentLocation() == null) {
            throw new EntityNotFoundException("Provider location not found");
        }
        return provider.getCurrentLocation();
    }
    public void toggleProviderAvailability(Long providerId, boolean available) {
        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new IllegalArgumentException("Provider not found"));

        provider.setAvailable(available); // لازم يكون عندك حقل available في Provider
        providerRepository.save(provider);
    }
}
