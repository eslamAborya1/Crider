package com.NTG.Cridir.controller;

import com.NTG.Cridir.DTOs.LocationUpdateRequest;
import com.NTG.Cridir.model.Location;
import com.NTG.Cridir.service.AuthService;
import com.NTG.Cridir.service.LocationService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/location")
public class LocationController {
    private final LocationService locationService;
    private final AuthService authService;
    public LocationController(LocationService s, AuthService authService) { this.locationService = s;
        this.authService = authService;
    }
    @PreAuthorize("hasRole('PROVIDER')")
    @PostMapping("/provider")
    public ResponseEntity<Void> updateProviderLocation(@RequestBody @Valid LocationUpdateRequest req) {
        locationService.updateProviderLocation(req);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/provider/{providerId}")
    public Location getProviderLocation(@PathVariable Long providerId) {
        return locationService.getProviderLocation(providerId);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping(path = "/stream/request/{requestId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamProviderForRequest(
            @PathVariable Long requestId,
            @RequestHeader("Authorization") String authHeader) {


        String token = authHeader.replace("Bearer ", "");
        Long customerId = authService.getCurrentUserId(token);

        SseEmitter emitter = new SseEmitter(0L);
        try {
            Location loc = locationService.getProviderLocationForRequest(requestId, customerId);
            emitter.send(SseEmitter.event().name("location").data(loc));
        } catch (Exception e) {
            emitter.completeWithError(e);
        }
        return emitter;
    }

}
