package com.NTG.Cridir.DTOs;

import com.NTG.Cridir.model.ServiceRequest.Status;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

// Used when we (backend) send data back to client (returning request details to Customer or Provider).
public record ServiceRequestResponse(
        Long requestId,
        String customerName,
        String providerName,
        String issueType,
        String carType,
        Status status,
        BigDecimal totalCost,
        OffsetDateTime requestTime,
        Double latitude,
        Double longitude
) {}
