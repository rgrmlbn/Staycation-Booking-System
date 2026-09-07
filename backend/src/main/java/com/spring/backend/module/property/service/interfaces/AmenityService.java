package com.spring.backend.module.property.service.interfaces;

import com.spring.backend.module.property.dto.request.AmenityCreateRequest;
import com.spring.backend.module.property.dto.request.AmenityUpdateRequest;
import com.spring.backend.module.property.dto.response.AmenityResponse;

import java.util.List;

public interface AmenityService {

    List<AmenityResponse> getAllAmenities();
    AmenityResponse getAmenityById(Long id);
    AmenityResponse createAmenity(AmenityCreateRequest request);
    AmenityResponse updateAmenity(Long id, AmenityUpdateRequest request);
    void deleteAmenity(Long id);


}
