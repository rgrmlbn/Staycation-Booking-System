package com.spring.backend.module.property.property.mapper;

import com.spring.backend.module.property.amenity.mapper.AmenityMapper;
import com.spring.backend.module.property.checkin.mapper.CheckInSlotMapper;
import com.spring.backend.module.property.property.dto.request.PropertyCreateRequest;
import com.spring.backend.module.property.amenity.dto.response.AmenityResponse;
import com.spring.backend.module.property.checkin.dto.response.CheckInSlotResponse;
import com.spring.backend.module.property.property.dto.response.PropertyDetailedResponse;
import com.spring.backend.module.property.property.dto.response.PropertySummaryResponse;
import com.spring.backend.module.property.property.entity.PropertyEntity;
import com.spring.backend.module.user.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PropertyMapper {

    private final AmenityMapper amenityMapper;
    private final CheckInSlotMapper checkInSlotMapper;

    public PropertyEntity toPropertyEntity(PropertyCreateRequest request, UserEntity user) {
        return PropertyEntity.builder()
                .user(user)
                .title(request.getTitle())
                .description(request.getDescription())
                .bedrooms(request.getBedrooms())
                .bathrooms(request.getBathrooms())
                .maxGuests(request.getMaxGuests())
                .address(request.getAddress())
                .build();
    }

    public PropertySummaryResponse toPropertySummaryResponse(PropertyEntity entity) {

        List<String> imageUrls = entity.getImages()
                .stream()
                .map(image -> image.getImageUrl())
                .toList();

        return PropertySummaryResponse.builder()
                .id(entity.getId())
                .hostId(entity.getUser().getId())
                .hostName(entity.getUser().getName())
                .title(entity.getTitle())
                .bedrooms(entity.getBedrooms())
                .maxGuests(entity.getMaxGuests())
                .address(entity.getAddress())
                .status(entity.getStatus())
                .imageUrls(imageUrls)
                .reviewScore(entity.getReviewScore())
                .reviewCount(entity.getReviewCount())
                .build();
    }

    public PropertyDetailedResponse toPropertyDetailedResponse(PropertyEntity entity) {

        List<String> imageUrls = entity.getImages()
                .stream()
                .map(image -> image.getImageUrl())
                .toList();

        List<AmenityResponse> amenities = entity.getAmenities()
                .stream()
                .map(amenity -> amenityMapper.toAmenityResponse(amenity))
                .toList();

        List<CheckInSlotResponse> checkInSlots = entity.getCheckInSlots()
                .stream()
                .map(checkInSlot -> checkInSlotMapper.toCheckInSlotResponse(checkInSlot))
                .toList();

        return PropertyDetailedResponse.builder()
                .id(entity.getId())
                .hostId(entity.getUser().getId())
                .hostName(entity.getUser().getName())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .bedrooms(entity.getBedrooms())
                .bathrooms(entity.getBathrooms())
                .maxGuests(entity.getMaxGuests())
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