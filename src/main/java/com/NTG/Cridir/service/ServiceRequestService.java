package com.NTG.Cridir.service;

import com.NTG.Cridir.DTOs.ServiceRequestDTO;
import com.NTG.Cridir.DTOs.ServiceRequestResponse;
import com.NTG.Cridir.exception.NotFoundException;
import com.NTG.Cridir.mapper.ServiceRequestMapper;
import com.NTG.Cridir.model.*;
import com.NTG.Cridir.model.Enum.Status;
import com.NTG.Cridir.repository.*;
import com.NTG.Cridir.util.GeoUtils;
import com.NTG.Cridir.util.PricingUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceRequestService {

    private final ServiceRequestRepository serviceRequestRepository;
    private final CustomerRepository customerRepository;
    private final ProviderRepository providerRepository;
    private final LocationRepository locationRepository;
    private final ServiceRequestMapper mapper;

    public ServiceRequestService(ServiceRequestRepository serviceRequestRepository,
                                 CustomerRepository customerRepository,
                                 ProviderRepository providerRepository,
                                 LocationRepository locationRepository,
                                 ServiceRequestMapper mapper) {
        this.serviceRequestRepository = serviceRequestRepository;
        this.customerRepository = customerRepository;
        this.providerRepository = providerRepository;
        this.locationRepository = locationRepository;
        this.mapper = mapper;
    }

    // Customer creates a request
    public ServiceRequestResponse createRequest(ServiceRequestDTO dto) {
        Customer customer = customerRepository.findById(dto.customerId())
                .orElseThrow(() -> new NotFoundException("Customer not found"));

        // location
        Location location = new Location();
        mapper.updateLocationFromDto(dto, location);
        locationRepository.save(location);

        // request
        ServiceRequest request = mapper.toEntity(dto);
        request.setCustomer(customer);
        request.setLocation(location);

        serviceRequestRepository.save(request);
        return mapper.toResponse(request);
    }

    public ServiceRequestResponse updateStatus(Long requestId, Status status) {
        ServiceRequest request = findRequestById(requestId);
        request.setStatus(status);
        serviceRequestRepository.save(request);
        return mapper.toResponse(request);
    }

    public ServiceRequestResponse getRequest(Long requestId) {
        return mapper.toResponse(findRequestById(requestId));
    }

public List<ServiceRequestResponse> getRequestsByCustomer(Long customerId) {
    return serviceRequestRepository.findByCustomerCustomerId(customerId)
            .stream()
            .map(mapper::toResponse)
            .toList();
}


    public List<ServiceRequestResponse> getRequestsByProvider(Long providerId) {
        return serviceRequestRepository.findByProviderProviderId(providerId)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ServiceRequestResponse> getPendingRequests() {
        return serviceRequestRepository.findByStatusAndProviderIsNull(Status.PENDING)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }


    // Provider accepts a request

    @Transactional
    public ServiceRequestResponse acceptRequest(Long requestId, Long providerId) {
        ServiceRequest request = serviceRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found"));

        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new NotFoundException("Provider not found"));


        request.setProvider(provider);
        request.setStatus(Status.ACCEPTED);

        //  calc cost
        double basePrice = PricingUtils.getBasePrice(request.getIssueType());

        double distance = GeoUtils.haversine(
                request.getLocation().getLatitude(),
                request.getLocation().getLongitude(),
                provider.getCurrentLocation().getLatitude(),
                provider.getCurrentLocation().getLongitude()
        );

        double distanceCost = distance * 10; // 10 per 1 km
        double totalCost = basePrice + distanceCost;

        request.setTotalCost(BigDecimal.valueOf(totalCost));

        serviceRequestRepository.save(request);

        return mapper.toResponse(request);
    }


    // 🔹 Helper Methods
    private ServiceRequest findRequestById(Long id) {
        return serviceRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Request not found"));
    }

    private void calculateCostAndEta(ServiceRequest request, Provider provider) {
        Location customerLoc = request.getLocation();
        Location providerLoc = provider.getCurrentLocation();

        if (providerLoc != null) {
            double distanceKm = GeoUtils.haversine(
                    providerLoc.getLatitude(), providerLoc.getLongitude(),
                    customerLoc.getLatitude(), customerLoc.getLongitude()
            );

            double baseCost = PricingUtils.getBasePrice(request.getIssueType());
            double distanceFee = distanceKm * 10;
            request.setTotalCost(BigDecimal.valueOf(baseCost + distanceFee));

            double speedKmh = 80.0;
            long etaSeconds = (long) ((distanceKm / speedKmh) * 3600);
            request.setEstimatedArrivalTime(java.time.Duration.ofSeconds(etaSeconds));
        }
    }
}

