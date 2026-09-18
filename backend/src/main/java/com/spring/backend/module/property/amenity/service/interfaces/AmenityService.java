package com.spring.backend.module.property.amenity.service.interfaces;

import com.spring.backend.module.property.amenity.dto.request.AmenityCreateRequest;
import com.spring.backend.module.property.amenity.dto.request.AmenityUpdateRequest;
import com.spring.backend.module.property.amenity.dto.response.AmenityResponse;

import java.util.List;

public interface AmenityService {

    List<AmenityResponse> getAllAmenities();
    AmenityResponse getAmenityById(Long id);
    AmenityResponse createAmenity(AmenityCreateRequest request);
    AmenityResponse updateAmenity(Long id, AmenityUpdateRequest request);
    void deleteAmenity(Long id);


}
