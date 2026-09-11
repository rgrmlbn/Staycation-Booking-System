package com.spring.backend.module.property.property.dto.response;

import com.spring.backend.module.property.property.enums.PropertyStatus;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PropertySummaryResponse {

    private Long id;
    private Long hostId;
    private String hostName;
    private String title;

    private Double pricePerNight;

    private Integer bedrooms;

    private String address;
    private PropertyStatus status;

    private List<String> imageUrls;

    private Double reviewScore;
    private Integer reviewCount;
}
