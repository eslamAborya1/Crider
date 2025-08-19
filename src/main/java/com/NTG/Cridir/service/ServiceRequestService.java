// service/ServiceRequestService.java
package com.NTG.Cridir.service;

import com.NTG.Cridir.DTOs.ServiceRequestDTO;
import com.NTG.Cridir.DTOs.ServiceRequestResponse;
import com.NTG.Cridir.model.*;
import com.NTG.Cridir.repository.*;

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

    public ServiceRequestService(ServiceRequestRepository serviceRequestRepository, CustomerRepository customerRepository, ProviderRepository providerRepository, LocationRepository locationRepository) {
        this.serviceRequestRepository = serviceRequestRepository;
        this.customerRepository = customerRepository;
        this.providerRepository = providerRepository;
        this.locationRepository = locationRepository;
    }

    // Create a request (Customer)
    public ServiceRequestResponse createRequest(ServiceRequestDTO dto) {
        Customer customer = customerRepository.findById(dto.customerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

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
        request.setStatus(ServiceRequest.Status.PENDING);
        request.setTotalCost(BigDecimal.valueOf(100)); // simple placeholder

        serviceRequestRepository.save(request);

        return mapToResponse(request);
    }

    // Get request by id
    public ServiceRequestResponse getRequest(Long requestId) {
        ServiceRequest request = serviceRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Request not found"));
        return mapToResponse(request);
    }

    // Get requests for a customer
    public List<ServiceRequestResponse> getRequestsByCustomer(Long customerId) {
        return serviceRequestRepository.findByCustomerCustomerId(customerId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    // Providers see pending requests
    public List<ServiceRequestResponse> getPendingRequests() {
        return serviceRequestRepository.findByStatus(ServiceRequest.Status.PENDING)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    // Provider accepts a request
    public ServiceRequestResponse acceptRequest(Long requestId, Long providerId) {
        ServiceRequest request = serviceRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Request not found"));

        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new EntityNotFoundException("Provider not found"));

        request.setProvider(provider);
        request.setStatus(ServiceRequest.Status.IN_PROGRESS);

        serviceRequestRepository.save(request);
        return mapToResponse(request);
    }

    // Update status (Provider)
    public ServiceRequestResponse updateStatus(Long requestId, ServiceRequest.Status status) {
        ServiceRequest request = serviceRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Request not found"));

        request.setStatus(status);
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
                request.getStatus(),
                request.getTotalCost(),
                request.getRequestTime(),
                request.getLocation().getLatitude(),
                request.getLocation().getLongitude()
        );
    }
}
