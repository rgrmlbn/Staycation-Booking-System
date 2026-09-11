package com.spring.backend.module.property.property.dto.response;

import com.spring.backend.module.property.property.entity.CheckInSlotEntity;
import com.spring.backend.module.property.property.enums.PropertyStatus;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class PropertyDetailedResponse {

    private Long id;
    private Long hostId;
    private String hostName;
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
    private List<CheckInSlotResponse> checkInSlots;

    private Double reviewScore;
    private Integer reviewCount;

}
