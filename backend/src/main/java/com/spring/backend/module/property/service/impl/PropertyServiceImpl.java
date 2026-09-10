package com.spring.backend.module.property.service.impl;

import com.spring.backend.exception.ResourceNotFoundException;
import com.spring.backend.module.property.dto.request.PropertyCreateRequest;
import com.spring.backend.module.property.dto.request.PropertyUpdateRequest;
import com.spring.backend.module.property.dto.response.PropertyDetailedResponse;
import com.spring.backend.module.property.dto.response.PropertySummaryResponse;
import com.spring.backend.module.property.entity.AmenityEntity;
import com.spring.backend.module.property.entity.ImageEntity;
import com.spring.backend.module.property.entity.PropertyEntity;
import com.spring.backend.module.property.enums.PropertyStatus;
import com.spring.backend.module.property.mapper.PropertyMapper;
import com.spring.backend.module.property.repository.AmenityRepository;
import com.spring.backend.module.property.repository.PropertyRepository;
import com.spring.backend.module.property.service.interfaces.PropertyService;
import com.spring.backend.module.shared.util.OwnershipVerifier;
import com.spring.backend.module.user.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.connection.RedisSubscribedConnectionException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PropertyServiceImpl implements PropertyService {

    private final PropertyRepository propertyRepository;
    private final PropertyMapper propertyMapper;
    private final AmenityRepository amenityRepository;
    private final OwnershipVerifier ownershipVerifier;

    @Override
    public Page<PropertySummaryResponse> getAllSummaryProperties(int page, int size, String title) {

        Pageable pageable = Pageable.ofSize(size).withPage(page);

        Page<PropertyEntity> property;

        if(title != null && !title.isBlank()) {
            property = propertyRepository.findByTitleContainingIgnoreCase(title, pageable);
        } else {
            property = propertyRepository.findAll(pageable);
        }

        return property.map(PropertyEntity-> propertyMapper.toSummaryResponse(PropertyEntity));
    }

    @Override
    public Page<PropertyDetailedResponse> getAllDetailedProperties(int page, int size, String title) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);

        Page<PropertyEntity> property;

        if(title != null && !title.isBlank()) {
            property = propertyRepository.findByTitleContainingIgnoreCase(title, pageable);
        } else {
            property = propertyRepository.findAll(pageable);
        }

        return property.map(PropertyEntity-> propertyMapper.toDetailedResponse(PropertyEntity));
    }

    @Override
    public Page<PropertyDetailedResponse> getAllDetailedPropertiesByStatus(int page, int size, PropertyStatus status) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);

        Page<PropertyEntity> property;

        if(status != null) {
            property = propertyRepository.findByStatus(status, pageable);
        } else {
            property = propertyRepository.findAll(pageable);
        }

        return property.map(PropertyEntity-> propertyMapper.toDetailedResponse(PropertyEntity));
    }

    @Override
    public PropertyDetailedResponse getPropertyById(Long id) {

        PropertyEntity property = propertyRepository.findById(id)
                .orElseThrow(() -> new RedisSubscribedConnectionException("Property"));

        return propertyMapper.toDetailedResponse(property);
    }

    @Override
    public PropertyDetailedResponse createProperty(PropertyCreateRequest request){

        UserEntity user = ownershipVerifier.getCurrentUser();

        PropertyEntity property = propertyMapper.toPropertyEntity(request, user);

        if (request.getAmenityIds() != null && !request.getAmenityIds().isEmpty()) {

            List<AmenityEntity> amenities = amenityRepository.findAllById(
                    request.getAmenityIds()
            );

            if (amenities.size() != request.getAmenityIds().size()) {
                throw new ResourceNotFoundException("One or more amenities");
            }

            property.setAmenities(amenities);
        }

        PropertyEntity savedProperty = propertyRepository.save(property);

        return propertyMapper.toDetailedResponse(savedProperty);
    }

    @Override
    public PropertyDetailedResponse updateProperty(Long id, PropertyUpdateRequest update) {

        PropertyEntity property = propertyRepository.findById(id)
                .orElseThrow(() -> new RedisSubscribedConnectionException("Property"));

        ownershipVerifier.verifyOwnershipOrAdmin(property.getUser());

        if(update.getTitle() != null && !update.getTitle().isBlank()) {
            property.setTitle(update.getTitle());
        }
        if(update.getDescription() != null && !update.getDescription().isBlank()) {
            property.setDescription(update.getDescription());
        }
        if(update.getPricePerNight() != null) {
            property.setPricePerNight(update.getPricePerNight());
        }
        if(update.getBedrooms() != null) {
            property.setBedrooms(update.getBedrooms());
        }
        if(update.getBathrooms() != null) {
            property.setBathrooms(update.getBathrooms());
        }
        if(update.getAirConditioning() != null) {
            property.setAirConditioning(update.getAirConditioning());
        }
        if(update.getAddress() != null && !update.getAddress().isBlank()) {
            property.setAddress(update.getAddress());
        }

        if (update.getImageUrls() != null) {

            List<ImageEntity> images = update.getImageUrls()
                    .stream()
                    .map(imageUrl -> ImageEntity.builder()
                            .imageUrl(imageUrl)
                            .property(property)
                            .build())
                    .toList();

            property.setImages(images);
        }

        if (update.getAmenityIds() != null) {

            List<AmenityEntity> amenities = amenityRepository.findAllById(update.getAmenityIds());

            property.setAmenities(amenities);
        }

        PropertyEntity updatedProperty = propertyRepository.save(property);

        return propertyMapper.toDetailedResponse(updatedProperty);
    }

    @Override
    public void deleteProperty(Long id) {

        PropertyEntity property = propertyRepository.findById(id)
                .orElseThrow(() -> new RedisSubscribedConnectionException("Property"));

        ownershipVerifier.verifyOwnershipOrAdmin(property.getUser());

        propertyRepository.delete(property);

    }
}
