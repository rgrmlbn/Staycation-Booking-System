package com.spring.backend.module.property.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class PropertyUpdateRequest {

    @NotNull(message = "Host ID is required")
    private Long hostId;

    @Size(min = 5, max = 100, message = "Title should be between 5 and 100 characters")
    private String title;

    @Size(min = 10, max = 200, message = "Description should be between 10 and 200 characters")
    private String description;

    @Min(value = 350, message = "Price per night should be 350 or more")
    private Double pricePerNight;

    @Min(value = 1, message = "Number of bedrooms should be 1 or more")
    private Integer bedrooms;

    @Min(value = 1, message = "Number of bathrooms should be 1 or more")
    private Integer bathrooms;

    private Boolean airConditioning;

    @Size(min = 10, max = 200, message = "Address should be between 10 and 200 characters")
    private String address;

    @Size(min = 1, message = "At least one image URL is required")
    private List<String> imageUrls;

    @Size(min = 1, message = "At least one amenity is required")
    private List<Long> amenityIds;

}
