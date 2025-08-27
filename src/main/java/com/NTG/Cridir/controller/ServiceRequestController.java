package com.NTG.Cridir.controller;

import com.NTG.Cridir.DTOs.ServiceRequestDTO;
import com.NTG.Cridir.DTOs.ServiceRequestResponse;
import com.NTG.Cridir.model.Enum.Status;
import com.NTG.Cridir.service.ServiceRequestService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/request")

public class ServiceRequestController {

    private final ServiceRequestService serviceRequestService;
    @Autowired
    public ServiceRequestController(ServiceRequestService serviceRequestService) {
        this.serviceRequestService = serviceRequestService;
    }

    // Customer creates request
    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping
    public ServiceRequestResponse createRequest(@RequestBody @Valid ServiceRequestDTO dto) {
        return serviceRequestService.createRequest(dto);

    }

    // Get request by id
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('PROVIDER')")
    @GetMapping("/{requestId}")
    public ServiceRequestResponse getRequest(@PathVariable Long requestId) {
        return serviceRequestService.getRequest(requestId);
    }

    // Get history of requests for one customer
    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/customer/{customerId}")
    public List<ServiceRequestResponse> getCustomerRequests(@PathVariable Long customerId) {
        return serviceRequestService.getRequestsByCustomer(customerId);
    }
    //ال requests الخاصه ب provider معين
    @PreAuthorize("hasRole('PROVIDER')")
    @GetMapping("/provider/{providerId}")
    public List<ServiceRequestResponse> getProviderRequests(@PathVariable Long providerId) {
        return serviceRequestService.getRequestsByProvider(providerId);
    }

    // Provider views pending requests
    @PreAuthorize("hasRole('PROVIDER')")
    @GetMapping("/pending")
    public List<ServiceRequestResponse> getPendingRequests() {
        return serviceRequestService.getPendingRequests();
    }

    // Provider accepts request
    @PreAuthorize("hasRole('PROVIDER')")
    @PatchMapping("/{requestId}/accept/{providerId}")
    public ServiceRequestResponse acceptRequest(@PathVariable Long requestId, @PathVariable Long providerId) {
        return serviceRequestService.acceptRequest(requestId, providerId);
    }

    // Provider updates status
    @PreAuthorize("hasRole('PROVIDER')")
    @PatchMapping("/{id}/status")
    public ServiceRequestResponse updateStatus(@PathVariable Long id,
                                               @RequestParam Status status) {
        return serviceRequestService.updateStatus(id, status);
    }
}
