package com.NTG.Cridir.mapper;

import com.NTG.Cridir.DTOs.LocationUpdateRequest;
import com.NTG.Cridir.model.Location;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;


@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LocationMapper {

    // لو عايز تعمل Location جديد من الـ DTO
    Location toEntity(LocationUpdateRequest req);

    // لو Location موجود وعايز تحدثه بالـ DTO
    void updateEntityFromDto(LocationUpdateRequest req, @MappingTarget Location location);
}
