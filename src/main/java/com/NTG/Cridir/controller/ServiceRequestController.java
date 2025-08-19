package com.NTG.Cridir.controller;

import com.NTG.Cridir.DTOs.ServiceRequestDTO;
import com.NTG.Cridir.DTOs.ServiceRequestResponse;
import com.NTG.Cridir.model.ServiceRequest;
import com.NTG.Cridir.service.ServiceRequestService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests")

public class ServiceRequestController {

    private final ServiceRequestService serviceRequestService;
    @Autowired
    public ServiceRequestController(ServiceRequestService serviceRequestService) {
        this.serviceRequestService = serviceRequestService;
    }

    // Customer creates request
    @PostMapping
    public ServiceRequestResponse createRequest(@RequestBody @Valid ServiceRequestDTO dto) {
        return serviceRequestService.createRequest(dto);
    }

    // Get request by id
    @GetMapping("/{id}")
    public ServiceRequestResponse getRequest(@PathVariable Long id) {
        return serviceRequestService.getRequest(id);
    }

    // Get requests by customer
    @GetMapping("/customer/{customerId}")
    public List<ServiceRequestResponse> getCustomerRequests(@PathVariable Long customerId) {
        return serviceRequestService.getRequestsByCustomer(customerId);
    }

    // Provider views pending requests
    @GetMapping("/pending")
    public List<ServiceRequestResponse> getPendingRequests() {
        return serviceRequestService.getPendingRequests();
    }

    // Provider accepts request
    @PatchMapping("/{id}/accept/{providerId}")
    public ServiceRequestResponse acceptRequest(@PathVariable Long id, @PathVariable Long providerId) {
        return serviceRequestService.acceptRequest(id, providerId);
    }

    // Provider updates status
    @PatchMapping("/{id}/status")
    public ServiceRequestResponse updateStatus(@PathVariable Long id,
                                               @RequestParam ServiceRequest.Status status) {
        return serviceRequestService.updateStatus(id, status);
    }
}
