package com.spring.backend.module.property.property.mapper;

import com.spring.backend.module.property.checkin.dto.request.CheckInSlotCreateRequest;
import com.spring.backend.module.property.property.dto.request.PropertyCreateRequest;
import com.spring.backend.module.property.amenity.dto.response.AmenityResponse;
import com.spring.backend.module.property.checkin.dto.response.CheckInSlotResponse;
import com.spring.backend.module.property.property.dto.response.PropertyDetailedResponse;
import com.spring.backend.module.property.property.dto.response.PropertySummaryResponse;
import com.spring.backend.module.property.checkin.entity.CheckInSlotEntity;
import com.spring.backend.module.property.property.entity.PropertyEntity;
import com.spring.backend.module.user.user.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

@Component
public class PropertyMapper {

    public CheckInSlotEntity toCheckInSlotEntity(CheckInSlotCreateRequest request, PropertyEntity property) {
        return CheckInSlotEntity.builder()
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .price(request.getPrice())
                .property(property)
                .build();
    }

    private long calculateDurationMinutes(LocalTime startTime, LocalTime endTime) {

        Duration duration = Duration.between(startTime, endTime);

        if (duration.isNegative()) {
            duration = duration.plusDays(1);
        }

        return duration.toMinutes();
    }

    public CheckInSlotResponse toCheckInSlotResponse(CheckInSlotEntity entity) {
        return CheckInSlotResponse.builder()
                .id(entity.getId())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .durationMinutes(calculateDurationMinutes(entity.getStartTime(), entity.getEndTime()))
                .price(entity.getPrice())
                .build();
    }

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