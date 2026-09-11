package com.spring.backend.module.property.mapper;

import com.spring.backend.module.property.dto.request.AmenityCreateRequest;
import com.spring.backend.module.property.dto.request.PropertyCreateRequest;
import com.spring.backend.module.property.dto.response.AmenityResponse;
import com.spring.backend.module.property.dto.response.PropertyDetailedResponse;
import com.spring.backend.module.property.dto.response.PropertySummaryResponse;
import com.spring.backend.module.property.entity.AmenityEntity;
import com.spring.backend.module.property.entity.ImageEntity;
import com.spring.backend.module.property.entity.PropertyEntity;
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

    public PropertyEntity toPropertyEntity(PropertyCreateRequest request, UserEntity user) {

        PropertyEntity property = PropertyEntity.builder()
                .user(user)
                .title(request.getTitle())
                .description(request.getDescription())
                .pricePerNight(request.getPricePerNight())
                .bedrooms(request.getBedrooms())
                .bathrooms(request.getBathrooms())
                .airConditioning(request.getAirConditioning())
                .address(request.getAddress())
                .build();

        List<ImageEntity> images = request.getImageUrls()
                .stream()
                .map(imageUrl -> ImageEntity.builder()
                        .imageUrl(imageUrl)
                        .property(property)
                        .build())
                .toList();

        property.setImages(images);

        return property;
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
                .reviewScore(entity.getReviewScore())
                .reviewCount(entity.getReviewCount())
                .build();
    }
}