package com.spring.backend.module.property.amenity.mapper;

import com.spring.backend.module.property.amenity.dto.request.AmenityCreateRequest;
import com.spring.backend.module.property.amenity.dto.response.AmenityResponse;
import com.spring.backend.module.property.amenity.entity.AmenityEntity;
import org.springframework.stereotype.Component;

@Component
public class AmenityMapper {

    public AmenityEntity toAmenityEntity(AmenityCreateRequest request) {
        return AmenityEntity.builder()
                .name(request.getName())
                .build();
    }

    public AmenityResponse toAmenityResponse(AmenityEntity entity) {
        return AmenityResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }
}
