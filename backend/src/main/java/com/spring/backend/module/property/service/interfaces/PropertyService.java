package com.spring.backend.module.property.service.interfaces;

import com.spring.backend.module.property.dto.request.PropertyCreateRequest;
import com.spring.backend.module.property.dto.request.PropertyUpdateRequest;
import com.spring.backend.module.property.dto.response.PropertyDetailedResponse;
import com.spring.backend.module.property.dto.response.PropertySummaryResponse;
import com.spring.backend.module.property.enums.PropertyStatus;
import org.springdoc.core.converters.models.Pageable;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PropertyService {

    Page<PropertySummaryResponse> getAllSummaryProperties(int page, int size, String title);
    Page<PropertyDetailedResponse> getAllDetailedProperties(int page, int size, String title);
    Page<PropertyDetailedResponse> getAllDetailedPropertiesByStatus(int page, int size, PropertyStatus status);
    PropertyDetailedResponse getPropertyById(Long id);
    PropertyDetailedResponse createProperty(PropertyCreateRequest request);
    PropertyDetailedResponse updateProperty(Long id, PropertyUpdateRequest update);
    void deleteProperty(Long id);

}
