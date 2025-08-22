package com.NTG.Cridir.service;

import com.NTG.Cridir.DTOs.ServiceRequestDTO;
import com.NTG.Cridir.DTOs.ServiceRequestResponse;
import com.NTG.Cridir.exception.NotFoundException;
import com.NTG.Cridir.model.*;
import com.NTG.Cridir.model.Enum.Status;
import com.NTG.Cridir.repository.*;
import com.NTG.Cridir.util.GeoUtils;
import com.NTG.Cridir.util.PricingUtils;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceRequestService {

    private final ServiceRequestRepository serviceRequestRepository;
    private final CustomerRepository customerRepository;
    private final ProviderRepository providerRepository;
    private final LocationRepository locationRepository;

    public ServiceRequestService(ServiceRequestRepository serviceRequestRepository,
                                 CustomerRepository customerRepository,
                                 ProviderRepository providerRepository,
                                 LocationRepository locationRepository) {
        this.serviceRequestRepository = serviceRequestRepository;
        this.customerRepository = customerRepository;
        this.providerRepository = providerRepository;
        this.locationRepository = locationRepository;
    }

    // Customer creates a request
    public ServiceRequestResponse createRequest(ServiceRequestDTO dto) {
        Customer customer = customerRepository.findById(dto.customerId())
                .orElseThrow(() -> new NotFoundException("Customer not found"));

        // Save location
        Location location = new Location();
        location.setLatitude(dto.latitude());
        location.setLongitude(dto.longitude());
        locationRepository.save(location);

        ServiceRequest request = new ServiceRequest();
        request.setCustomer(customer);
        request.setLocation(location);
        request.setIssueType(dto.issueType());
        request.setCarType(dto.carType());
        request.setCarModelYear(dto.carModelYear());
        request.setStatus(Status.PENDING);

        //  cost placeholder until provider accepts
        request.setTotalCost(BigDecimal.ZERO);

        serviceRequestRepository.save(request);
        return mapToResponse(request);
    }

    public ServiceRequestResponse updateStatus(Long requestId, Status status) {
        ServiceRequest request = serviceRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Request not found"));

        request.setStatus(status);
        serviceRequestRepository.save(request);

        return mapToResponse(request);
    }

    // Get request by ID
    public ServiceRequestResponse getRequest(Long requestId) {
        ServiceRequest request = serviceRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found"));
        return mapToResponse(request);
    }

    // Get requests for a customer
    public List<ServiceRequestResponse> getRequestsByCustomer(Long customerId) {
        return serviceRequestRepository.findByCustomerCustomerId(customerId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ServiceRequestResponse> getRequestsByProvider(Long providerId) {
        return serviceRequestRepository.findByProviderProviderId(providerId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    // Providers see pending requests
    public List<ServiceRequestResponse> getPendingRequests() {
        return serviceRequestRepository.findByStatus(Status.PENDING)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Provider accepts a request

        // ✅ Provider accepts a request
        public ServiceRequestResponse acceptRequest(Long requestId, Long providerId) {
            ServiceRequest request = serviceRequestRepository.findById(requestId)
                    .orElseThrow(() -> new NotFoundException("Request not found"));

            Provider provider = providerRepository.findById(providerId)
                    .orElseThrow(() -> new NotFoundException("Provider not found"));

            request.setProvider(provider);
            request.setStatus(Status.ACCEPTED);

            Location customerLoc = request.getLocation();
            Location providerLoc = provider.getCurrentLocation();

            if (providerLoc != null) {
                // 1️⃣ Calculate distance
                double distanceKm = GeoUtils.haversine(
                        providerLoc.getLatitude(), providerLoc.getLongitude(),
                        customerLoc.getLatitude(), customerLoc.getLongitude()
                );

                // 2️⃣ Get base cost from issueType
                double baseCost = PricingUtils.getBasePrice(request.getIssueType());

                // 3️⃣ Distance fee
                double distanceFee = distanceKm * 10; // example: 10 EGP/km

                // 4️⃣ Total cost
                double totalCost = baseCost + distanceFee;
                request.setTotalCost(BigDecimal.valueOf(totalCost));

                // 5️⃣ ETA logic (assume 40 km/h speed)
                double speedKmh = 40.0;
                long etaSeconds = (long) ((distanceKm / speedKmh) * 3600);
                request.setEstimatedArrivalTime(java.time.Duration.ofSeconds(etaSeconds));
            }

            serviceRequestRepository.save(request);
            return mapToResponse(request);
        }

        // Mapper
        private ServiceRequestResponse mapToResponse(ServiceRequest request) {
            return new ServiceRequestResponse(
                    request.getRequestId(),
                    request.getCustomer().getName(),
                    request.getProvider() != null ? request.getProvider().getName() : null,
                    request.getIssueType(),
                    request.getCarType(),
                    request.getCarModelYear(),
                    request.getStatus(),           //map struct
                    request.getTotalCost(),
                    request.getRequestTime(),
                    request.getLocation().getLatitude(),
                    request.getLocation().getLongitude(),
                    request.getEstimatedArrivalSeconds()

            );
        }
    }
