package com.NTG.Cridir.mapper;

import com.NTG.Cridir.DTOs.ServiceRequestDTO;
import com.NTG.Cridir.DTOs.ServiceRequestResponse;
import com.NTG.Cridir.model.Location;
import com.NTG.Cridir.model.ServiceRequest;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface ServiceRequestMapper {

    // من DTO → Entity (بدون customer والـ location لأنهم بييجوا من DB)
    @Mapping(target = "requestId", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "provider", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "totalCost", expression = "java(java.math.BigDecimal.ZERO)")
    ServiceRequest toEntity(ServiceRequestDTO dto);

    // Entity → Response DTO
    @Mapping(target = "customerName", source = "customer.user.name")
    @Mapping(target = "providerName", source = "provider.user.name", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    @Mapping(target = "latitude", source = "location.latitude")
    @Mapping(target = "longitude", source = "location.longitude")
    ServiceRequestResponse toResponse(ServiceRequest entity);

    // تحديث الـ location من DTO
    @Mapping(target = "locationId", ignore = true)
    @Mapping(target = "timestamp", ignore = true)
    void updateLocationFromDto(ServiceRequestDTO dto, @MappingTarget Location location);
}
