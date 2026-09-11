package com.spring.backend.module.property.property.mapper;

import com.spring.backend.module.property.property.dto.request.AmenityCreateRequest;
import com.spring.backend.module.property.property.dto.request.CheckInSlotRequest;
import com.spring.backend.module.property.property.dto.request.PropertyCreateRequest;
import com.spring.backend.module.property.property.dto.response.AmenityResponse;
import com.spring.backend.module.property.property.dto.response.CheckInSlotResponse;
import com.spring.backend.module.property.property.dto.response.PropertyDetailedResponse;
import com.spring.backend.module.property.property.dto.response.PropertySummaryResponse;
import com.spring.backend.module.property.property.entity.AmenityEntity;
import com.spring.backend.module.property.property.entity.CheckInSlotEntity;
import com.spring.backend.module.property.property.entity.ImageEntity;
import com.spring.backend.module.property.property.entity.PropertyEntity;
import com.spring.backend.module.user.user.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.util.List;

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

    public CheckInSlotEntity toCheckInSlotEntity(CheckInSlotRequest request, PropertyEntity property) {
        return CheckInSlotEntity.builder()
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .property(property)
                .build();
    }

    public CheckInSlotResponse toCheckInSlotResponse(CheckInSlotEntity entity) {
        return CheckInSlotResponse.builder()
                .id(entity.getId())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .build();
    }

    public PropertyEntity toPropertyEntity(PropertyCreateRequest request, UserEntity user) {
        return PropertyEntity.builder()
                .user(user)
                .title(request.getTitle())
                .description(request.getDescription())
                .pricePerNight(request.getPricePerNight())
                .bedrooms(request.getBedrooms())
                .bathrooms(request.getBathrooms())
                .airConditioning(request.getAirConditioning())
                .address(request.getAddress())
                .build();
    }

    public PropertySummaryResponse toSummaryResponse(PropertyEntity entity) {

        List<String> imageUrls = entity.getImages()
                .stream()
                .map(image -> image.getImageUrl())
                .toList();

        return PropertySummaryResponse.builder()
                .id(entity.getId())
                .hostId(entity.getUser().getId())
                .hostName(entity.getUser().getName())
                .title(entity.getTitle())
                .pricePerNight(entity.getPricePerNight())
                .bedrooms(entity.getBedrooms())
                .address(entity.getAddress())
                .status(entity.getStatus())
                .imageUrls(imageUrls)
                .reviewScore(entity.getReviewScore())
                .reviewCount(entity.getReviewCount())
                .build();
    }

    public PropertyDetailedResponse toDetailedResponse(PropertyEntity entity) {

        List<String> imageUrls = entity.getImages()
                .stream()
                .map(image -> image.getImageUrl())
                .toList();

        List<AmenityResponse> amenities = entity.getAmenities()
                .stream()
                .map(amenity -> toAmenityResponse(amenity))
                .toList();

        List<CheckInSlotResponse> checkInSlots = entity.getCheckInSlots()
                .stream()
                .map(checkInSlot -> toCheckInSlotResponse(checkInSlot))
                .toList();

        return PropertyDetailedResponse.builder()
                .id(entity.getId())
                .hostId(entity.getUser().getId())
                .hostName(entity.getUser().getName())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .pricePerNight(entity.getPricePerNight())
                .bedrooms(entity.getBedrooms())
                .bathrooms(entity.getBathrooms())
                .airConditioning(entity.getAirConditioning())
                .address(entity.getAddress())
                .status(entity.getStatus())
                .imageUrls(imageUrls)
                .amenities(amenities)
                .checkInSlots(checkInSlots)
                .reviewScore(entity.getReviewScore())
                .reviewCount(entity.getReviewCount())
                .build();
    }
}