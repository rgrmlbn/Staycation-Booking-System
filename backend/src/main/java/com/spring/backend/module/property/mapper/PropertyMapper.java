package com.spring.backend.module.property.mapper;

import com.spring.backend.module.property.dto.request.AmenityCreateRequest;
import com.spring.backend.module.property.dto.response.AmenityResponse;
import com.spring.backend.module.property.dto.response.PropertyDetailedResponse;
import com.spring.backend.module.property.dto.response.PropertySummaryResponse;
import com.spring.backend.module.property.entity.AmenityEntity;
import com.spring.backend.module.property.entity.PropertyEntity;
import org.springframework.stereotype.Component;

@Component
public class PropertyMapper {

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

    public PropertySummaryResponse toSummaryResponse(PropertyEntity entity) {
        return PropertySummaryResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .pricePerNight(entity.getPricePerNight())
                .bedrooms(entity.getBedrooms())
                .address(entity.getAddress())
                .status(entity.getStatus())
                .imageUrls(
                        entity.getImages()
                                .stream()
                                .map(image -> image.getImageUrl())
                                .toList()
                )
                .reviewScore(entity.getReviewScore())
                .reviewCount(entity.getReviewCount())
                .build();
    }

    public PropertyDetailedResponse toDetailedResponse(PropertyEntity entity) {
        return PropertyDetailedResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .pricePerNight(entity.getPricePerNight())
                .bedrooms(entity.getBedrooms())
                .bathrooms(entity.getBathrooms())
                .airConditioning(entity.getAirConditioning())
                .address(entity.getAddress())
                .status(entity.getStatus())
                .imageUrls(
                        entity.getImages()
                                .stream()
                                .map(image -> image.getImageUrl())
                                .toList()
                )
                .amenities(
                        entity.getAmenities()
                                .stream()
                                .map(amenity -> toAmenityResponse(amenity))
                                .toList()
                )
                .reviewScore(entity.getReviewScore())
                .reviewCount(entity.getReviewCount())
                .build();
    }
}