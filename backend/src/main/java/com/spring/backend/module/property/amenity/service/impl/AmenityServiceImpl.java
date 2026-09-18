package com.spring.backend.module.property.amenity.service.impl;

import com.spring.backend.exception.common.ResourceNotFoundException;
import com.spring.backend.exception.property.property.DuplicateAmenityException;
import com.spring.backend.module.property.amenity.dto.request.AmenityCreateRequest;
import com.spring.backend.module.property.amenity.dto.request.AmenityUpdateRequest;
import com.spring.backend.module.property.amenity.dto.response.AmenityResponse;
import com.spring.backend.module.property.amenity.entity.AmenityEntity;
import com.spring.backend.module.property.amenity.mapper.AmenityMapper;
import com.spring.backend.module.property.property.mapper.PropertyMapper;
import com.spring.backend.module.property.amenity.repository.AmenityRepository;
import com.spring.backend.module.property.amenity.service.interfaces.AmenityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AmenityServiceImpl implements AmenityService {

    private final AmenityRepository amenityRepository;
    private final PropertyMapper propertyMapper;
    private final AmenityMapper amenityMapper;

    // Get all amenities
    @Override
    public List<AmenityResponse> getAllAmenities() {
        return amenityRepository.findAll()
                .stream()
                .map(amenity -> amenityMapper.toAmenityResponse(amenity))
                .toList();
    }

    // Get an amenity by its ID, throwing an exception if not found
    @Override
    public AmenityResponse getAmenityById(Long id) {

        AmenityEntity entity = amenityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Amenity"));

        return amenityMapper.toAmenityResponse(entity);
    }

    // Create a new amenity, checking for duplicate names before saving
    @Override
    public AmenityResponse createAmenity(AmenityCreateRequest request) {

        if(amenityRepository.existsByName(request.getName())) {
            throw new DuplicateAmenityException();
        }

        AmenityEntity entity = amenityMapper.toAmenityEntity(request);

        AmenityEntity savedEntity = amenityRepository.save(entity);

        return amenityMapper.toAmenityResponse(savedEntity);
    }

    // Update an amenity by its ID, throwing an exception if not found
    @Override
    public AmenityResponse updateAmenity(Long id, AmenityUpdateRequest update) {

        AmenityEntity entity = amenityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Amenity"));

        // Update the name if it is not null or blank
        if(update.getName() != null && !update.getName().isBlank()) {
            entity.setName(update.getName());
        }

        AmenityEntity updatedEntity = amenityRepository.save(entity);

        return amenityMapper.toAmenityResponse(updatedEntity);
    }

    // Delete an amenity by its ID, throwing an exception if not found
    @Override
    public void deleteAmenity(Long id) {

        AmenityEntity entity = amenityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Amenity"));

        amenityRepository.delete(entity);
    }
}
