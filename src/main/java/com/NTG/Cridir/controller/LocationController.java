package com.NTG.Cridir.controller;

import com.NTG.Cridir.DTOs.LocationUpdateRequest;
import com.NTG.Cridir.model.Location;
import com.NTG.Cridir.service.LocationService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/location")
public class LocationController {
    private final LocationService locationService;
    public LocationController(LocationService s) { this.locationService = s; }
    @PreAuthorize("hasRole('PROVIDER')")
    @PutMapping("/provider")
    public void updateProviderLocation(@RequestBody @Valid LocationUpdateRequest req) {
        locationService.updateProviderLocation(req);
    }
    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/provider/{providerId}")
    public Location getProviderLocation(@PathVariable Long providerId) {
        return locationService.getProviderLocation(providerId);
    }

    // Optional: simple SSE stream (frontend subscribes and polls via events)
    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping(path = "/stream/provider/{providerId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamProvider(@PathVariable Long providerId) {
        // naive demo stream (push one snapshot now then complete)
        SseEmitter emitter = new SseEmitter(0L);
        try {
            Location loc = locationService.getProviderLocation(providerId);
            emitter.send(SseEmitter.event().name("location").data(loc));
            emitter.complete();
        } catch (Exception e) {
            emitter.completeWithError(e);
        }
        return emitter;
    }
}
