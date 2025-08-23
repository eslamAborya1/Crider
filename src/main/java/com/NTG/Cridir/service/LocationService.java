package com.NTG.Cridir.service;

import com.NTG.Cridir.DTOs.LocationUpdateRequest;
import com.NTG.Cridir.Websocket.ProviderLocationSocketHandler;
import com.NTG.Cridir.exception.NotFoundException;
import com.NTG.Cridir.mapper.LocationMapper;
import com.NTG.Cridir.model.Location;
import com.NTG.Cridir.model.Provider;
import com.NTG.Cridir.model.ServiceRequest;
import com.NTG.Cridir.repository.LocationRepository;
import com.NTG.Cridir.repository.ProviderRepository;
import com.NTG.Cridir.repository.ServiceRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LocationService {

    private final LocationRepository locationRepository;
    private final ProviderRepository providerRepository;
    private final ProviderLocationSocketHandler socketHandler;
    private final LocationMapper locationMapper;
    private final ServiceRequestRepository serviceRequestRepository;

    public LocationService(LocationRepository locationRepository,
                           ProviderRepository providerRepository,
                           ProviderLocationSocketHandler socketHandler,
                           LocationMapper locationMapper, ServiceRequestRepository serviceRequestRepository) {
        this.locationRepository = locationRepository;
        this.providerRepository = providerRepository;
        this.socketHandler = socketHandler;
        this.locationMapper = locationMapper;

        this.serviceRequestRepository = serviceRequestRepository;
    }

    @Transactional
    public void updateProviderLocation(LocationUpdateRequest req) {
        Provider provider = providerRepository.findById(req.providerId())
                .orElseThrow(() -> new NotFoundException("Provider not found"));

        Location loc = provider.getCurrentLocation();
        if (loc == null) {
            loc = locationMapper.toEntity(req);
        } else {
            locationMapper.updateEntityFromDto(req, loc);
        }

        loc = locationRepository.save(loc);
        provider.setCurrentLocation(loc);
        providerRepository.save(provider);

        socketHandler.broadcastProviderLocation(provider.getProviderId(), loc);
    }

    @Transactional(readOnly = true)
    public Location getProviderLocation(Long providerId) {
        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new NotFoundException("Provider not found"));
        if (provider.getCurrentLocation() == null) {
            throw new NotFoundException("Provider location not found");
        }
        return provider.getCurrentLocation();
    }
    @Transactional(readOnly = true)
    public Location getProviderLocationForRequest(Long requestId, Long customerId) {
        ServiceRequest request = serviceRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found"));


        if (!request.getCustomer().getUser().getUserId().equals(customerId)) {
            throw new RuntimeException("Unauthorized to view this provider location");
        }

        Provider provider = request.getProvider();
        if (provider == null || provider.getCurrentLocation() == null) {
            throw new NotFoundException("Provider location not found");
        }

        return provider.getCurrentLocation();
    }

    public void toggleProviderAvailability(Long providerId, boolean available) {
        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new NotFoundException("Provider not found"));

        provider.setAvailabilityStatus(available);
        providerRepository.save(provider);
    }

}
