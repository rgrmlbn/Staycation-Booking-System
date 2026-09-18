package com.spring.backend.module.property.amenity.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AmenityResponse {

    private Long id;
    private String name;

}
