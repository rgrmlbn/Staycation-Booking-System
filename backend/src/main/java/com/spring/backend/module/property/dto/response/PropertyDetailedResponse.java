package com.spring.backend.module.property.dto.response;

import com.spring.backend.module.property.enums.PropertyStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PropertyDetailedResponse {

    private Long id;
    private Long hostId;
    private String title;
    private String description;
    private Double pricePerNight;

    private Integer bedrooms;
    private Integer bathrooms;
    private Boolean airConditioning;

    private String address;
    private PropertyStatus status;

    private List<String> imageUrls;
    private List<AmenityResponse> amenities;

    private Double reviewScore;
    private Integer reviewCount;

}
