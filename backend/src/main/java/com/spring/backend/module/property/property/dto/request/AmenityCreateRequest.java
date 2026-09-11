package com.spring.backend.module.property.property.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class AmenityCreateRequest {

    @NotBlank(message = "Amenity name is required")
    @Size(min = 3, max = 50, message = "Amenity name should be between 3 and 50 characters")
    private String name;

}
