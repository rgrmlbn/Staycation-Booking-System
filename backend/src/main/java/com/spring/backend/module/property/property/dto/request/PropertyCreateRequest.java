package com.spring.backend.module.property.property.dto.request;
import jakarta.validation.Valid;
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
public class PropertyCreateRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 5, max = 100, message = "Title should be between 5 and 100 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 200, message = "Description should be between 10 and 200 characters")
    private String description;

    @NotNull(message = "Price per night is required")
    @Min(value = 350, message = "Price per night should be 350 or more")
    private Double pricePerNight;

    @NotNull(message = "Bedrooms is required")
    @Min(value = 1, message = "Number of bedrooms should be 1 or more")
    private Integer bedrooms;

    @NotNull(message = "Bathrooms is required")
    @Min(value = 1, message = "Number of bathrooms should be 1 or more")
    private Integer bathrooms;

    @NotNull(message = "Air conditioning information is required")
    private Boolean airConditioning;

    @NotBlank(message = "Address is required")
    @Size(min = 10, max = 200, message = "Address should be between 10 and 200 characters")
    private String address;

    @NotNull(message = "Images are required")
    @Size(min = 1, message = "At least one image URL is required")
    private List<String> imageUrls;

    @NotNull(message = "Amenities are required")
    @Size(min = 1, message = "At least one amenity is required")
    private List<Long> amenityIds;

    @Valid
    private List<CheckInSlotRequest> checkInSlots;
}
