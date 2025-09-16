package com.NTG.Cridir.service;

import com.NTG.Cridir.DTOs.ServiceRequestDTO;
import com.NTG.Cridir.DTOs.ServiceRequestResponse;
import com.NTG.Cridir.Websocket.RideSocketHandler;
import com.NTG.Cridir.exception.NotFoundException;
import com.NTG.Cridir.mapper.ServiceRequestMapper;
import com.NTG.Cridir.model.*;
import com.NTG.Cridir.model.Enum.Status;
import com.NTG.Cridir.repository.*;
import com.NTG.Cridir.util.GeoUtils;
import com.NTG.Cridir.util.PricingUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Slf4j
@Service
public class ServiceRequestService {

    private final ServiceRequestRepository serviceRequestRepository;
    private final CustomerRepository customerRepository;
    private final ProviderRepository providerRepository;
    private final RideSocketHandler rideSocketHandler;
    private final LocationRepository locationRepository;
    private final ServiceRequestMapper mapper;


    public ServiceRequestService(ServiceRequestRepository serviceRequestRepository,
                                 CustomerRepository customerRepository,
                                 ProviderRepository providerRepository,
                                 @Lazy RideSocketHandler rideSocketHandler, LocationRepository locationRepository,
                                 ServiceRequestMapper mapper) {
        this.serviceRequestRepository = serviceRequestRepository;
        this.customerRepository = customerRepository;
        this.providerRepository = providerRepository;
        this.rideSocketHandler = rideSocketHandler;
        this.locationRepository = locationRepository;
        this.mapper = mapper;
    }

    // Customer creates a request
//    public ServiceRequestResponse createRequest(ServiceRequestDTO dto) {
//        Customer customer = customerRepository.findById(dto.)
//                .orElseThrow(() -> new NotFoundException("Customer not found"));
//
//        // location
//        Location location = new Location();
//        mapper.updateLocationFromDto(dto, location);
//        locationRepository.save(location);
//
//        // request
//        ServiceRequest request = mapper.toEntity(dto);
//        request.setCustomer(customer);
//        request.setLocation(location);
//
//        serviceRequestRepository.save(request);
//        return mapper.toResponse(request);
//    }0
    public ServiceRequestResponse createRequest(ServiceRequestDTO dto, Long userId) {
        ServiceRequest request = mapper.toEntity(dto);

        // اربط الـ customer بالـ userId
        Customer customer = customerRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        request.setCustomer(customer);

        // location
        Location location = new Location();
        mapper.updateLocationFromDto(dto, location);
        location = locationRepository.save(location);
        request.setLocation(location);

        // حط baseCost بس
        double baseCost = PricingUtils.getBasePrice(dto.issueType());
        request.setTotalCost(BigDecimal.valueOf(baseCost));

        ServiceRequest saved = serviceRequestRepository.save(request);
        return mapper.toResponse(saved);
    }





    public ServiceRequestResponse updateStatus(Long requestId, Status status) {
        ServiceRequest request = findRequestById(requestId);
        request.setStatus(status);
        serviceRequestRepository.save(request);
        try {
            rideSocketHandler.broadcastGeneric(requestId, Map.of(
                    "type", "STATUS_UPDATE",
                    "status", status.name()
            ));
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        ServiceRequest request = findRequestById(requestId);

        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new NotFoundException("Provider not found"));

        request.setProvider(provider);
        request.setStatus(Status.ACCEPTED);

        //  حساب التكلفة و ETA في مكان واحد
        calculateCostAndEta(request, provider);

        serviceRequestRepository.save(request);
        ServiceRequestResponse response = mapper.toResponse(request);

        try {
            rideSocketHandler.broadcastGeneric(requestId, Map.of(
                    "type", "STATUS_UPDATE",
                    "status", Status.ACCEPTED.name(),
                    "providerId", providerId,
                    "eta", response.estimatedArrivalSeconds(),
                    "cost", response.totalCost()
            ));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }
    @Transactional
    public ServiceRequestResponse cancelRequest(Long requestId, Long customerId) {
        ServiceRequest request = findRequestById(requestId);

        if (!request.getCustomer().getCustomerId().equals(customerId)) {
            throw new RuntimeException("You can only cancel your own requests");
        }

        request.setStatus(Status.CANCELLED);
        serviceRequestRepository.save(request);

        ServiceRequestResponse response = mapper.toResponse(request);

        try {
            rideSocketHandler.broadcastGeneric(requestId, Map.of(
                    "type", "CANCEL",
                    "status", Status.CANCELLED.name(),
                    "message", "Customer canceled the ride"
            ));
        } catch (Exception e) {
            log.error("Failed to broadcast cancel for request {}", requestId, e);
        }

        return response;
    }



    //  Helper Methods
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

            // خد baseCost اللي اتحسب قبل كده
            double baseCost = request.getTotalCost().doubleValue();

            // distanceFee
            double distanceFee = distanceKm * 10;

            // اجمع الاتنين
            request.setTotalCost(BigDecimal.valueOf(baseCost + distanceFee));

            // ETA
            double speedKmh = 80.0;
            long etaSeconds = (long) ((distanceKm / speedKmh) * 3600);
            request.setEstimatedArrivalTime(java.time.Duration.ofSeconds(etaSeconds));
        }
    }


}

