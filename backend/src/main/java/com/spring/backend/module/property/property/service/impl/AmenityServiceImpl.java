package com.spring.backend.module.property.property.service.impl;

import com.spring.backend.exception.common.ResourceNotFoundException;
import com.spring.backend.exception.property.property.DuplicateAmenityException;
import com.spring.backend.module.property.property.dto.request.AmenityCreateRequest;
import com.spring.backend.module.property.property.dto.request.AmenityUpdateRequest;
import com.spring.backend.module.property.property.dto.response.AmenityResponse;
import com.spring.backend.module.property.property.entity.AmenityEntity;
import com.spring.backend.module.property.property.mapper.PropertyMapper;
import com.spring.backend.module.property.property.repository.AmenityRepository;
import com.spring.backend.module.property.property.service.interfaces.AmenityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AmenityServiceImpl implements AmenityService {

    private final AmenityRepository amenityRepository;
    private final PropertyMapper propertyMapper;

    @Override
    public List<AmenityResponse> getAllAmenities() {
        return amenityRepository.findAll()
                .stream()
                .map(amenity -> propertyMapper.toAmenityResponse(amenity))
                .toList();
    }

    @Override
    public AmenityResponse getAmenityById(Long id) {

        AmenityEntity entity = amenityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Amenity"));

        return propertyMapper.toAmenityResponse(entity);
    }

    @Override
    public AmenityResponse createAmenity(AmenityCreateRequest request) {

        if(amenityRepository.existsByName(request.getName())) {
            throw new DuplicateAmenityException();
        }

        AmenityEntity entity = propertyMapper.toAmenityEntity(request);

        AmenityEntity savedEntity = amenityRepository.save(entity);

        return propertyMapper.toAmenityResponse(savedEntity);
    }

    @Override
    public AmenityResponse updateAmenity(Long id, AmenityUpdateRequest update) {

        AmenityEntity entity = amenityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Amenity"));

        if(update.getName() != null && !update.getName().isBlank()) {
            entity.setName(update.getName());
        }

        AmenityEntity updatedEntity = amenityRepository.save(entity);

        return propertyMapper.toAmenityResponse(updatedEntity);
    }

    @Override
    public void deleteAmenity(Long id) {

        AmenityEntity entity = amenityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Amenity"));

        amenityRepository.delete(entity);
    }
}
