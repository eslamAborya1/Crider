package com.NTG.Cridir.mapper;

import com.NTG.Cridir.DTOs.ProviderResponseDTO;
import com.NTG.Cridir.model.Provider;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface ProviderMapper {

    @Mapping(source = "providerId", target = "providerId")
    @Mapping(source = "user.name", target = "name")
    @Mapping(source = "user.phone", target = "phone")
    @Mapping(source = "availabilityStatus", target = "availabilityStatus")
    @Mapping(source = "currentLocation.latitude", target = "latitude")
    @Mapping(source = "currentLocation.longitude", target = "longitude")
    ProviderResponseDTO toResponseDTO(Provider provider);
}