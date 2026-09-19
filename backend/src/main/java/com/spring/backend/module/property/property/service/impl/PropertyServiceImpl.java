package com.spring.backend.module.property.property.service.impl;

import com.spring.backend.exception.common.ResourceNotFoundException;
import com.spring.backend.exception.property.property.DuplicateAmenityException;
import com.spring.backend.exception.property.property.DuplicateImageException;
import com.spring.backend.module.property.checkin.mapper.CheckInSlotMapper;
import com.spring.backend.module.property.property.dto.request.PropertyCreateRequest;
import com.spring.backend.module.property.property.dto.request.PropertyUpdateRequest;
import com.spring.backend.module.property.property.dto.response.PropertyDetailedResponse;
import com.spring.backend.module.property.property.dto.response.PropertySummaryResponse;
import com.spring.backend.module.property.amenity.entity.AmenityEntity;
import com.spring.backend.module.property.checkin.entity.CheckInSlotEntity;
import com.spring.backend.module.property.property.entity.ImageEntity;
import com.spring.backend.module.property.property.entity.PropertyEntity;
import com.spring.backend.module.property.property.enums.PropertyStatus;
import com.spring.backend.module.property.property.mapper.PropertyMapper;
import com.spring.backend.module.property.amenity.repository.AmenityRepository;
import com.spring.backend.module.property.property.repository.PropertyRepository;
import com.spring.backend.module.property.property.service.interfaces.PropertyService;
import com.spring.backend.module.shared.util.OwnershipVerifier;
import com.spring.backend.module.user.user.entity.UserEntity;
import com.spring.backend.module.user.user.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PropertyServiceImpl implements PropertyService {

    private final PropertyRepository propertyRepository;
    private final PropertyMapper propertyMapper;
    private final CheckInSlotMapper checkInSlotMapper;
    private final AmenityRepository amenityRepository;
    private final OwnershipVerifier ownershipVerifier;


    // Get all properties owned by the current user, with optional title filtering and pagination support
    @Override
    public Page<PropertyDetailedResponse> getOwnedProperties(int page, int size, String title) {

        UserEntity user = ownershipVerifier.getCurrentUser();

        if (!user.getRole().equals(UserRole.HOST)) {
            throw new AccessDeniedException(
                    "You do not have permission to access this resource."
            );
        }

        Pageable pageable = Pageable.ofSize(size).withPage(page);

        Page<PropertyEntity> property;

        // Filter properties by title when a search title is provided
        if (title != null && !title.isBlank()) {
            property = propertyRepository.findAllByUserIdAndTitleContainingIgnoreCase(
                    user.getId(),
                    title,
                    pageable
            );
        } else {
            property = propertyRepository.findAllByUserId(
                    user.getId(),
                    pageable
            );
        }

        return property.map(propertyEntity -> propertyMapper.toPropertyDetailedResponse(propertyEntity));
    }

    // Get all summary properties with pagination support, optionally filtered by title
    @Override
    public Page<PropertySummaryResponse> getAllSummaryProperties(int page, int size, String title) {

        Pageable pageable = Pageable.ofSize(size).withPage(page);

        Page<PropertyEntity> property;

        // Filter properties by title when a search title is provided
        if(title != null && !title.isBlank()) {
            property = propertyRepository.findByTitleContainingIgnoreCase(title, pageable);
        } else {
            property = propertyRepository.findAll(pageable);
        }

        return property.map(propertyEntity -> propertyMapper.toPropertySummaryResponse(propertyEntity));
    }

    // Get all detailed properties, with optional title filtering and pagination support
    @Override
    public Page<PropertyDetailedResponse> getAllDetailedProperties(int page, int size, String title) {

        Pageable pageable = Pageable.ofSize(size).withPage(page);

        Page<PropertyEntity> property;

        // Filter properties by title when a search title is provided
        if(title != null && !title.isBlank()) {
            property = propertyRepository.findByTitleContainingIgnoreCase(title, pageable);
        } else {
            property = propertyRepository.findAll(pageable);
        }

        return property.map(propertyEntity -> propertyMapper.toPropertyDetailedResponse(propertyEntity));
    }

    // Get all detailed properties filtered by status, with pagination support
    @Override
    public Page<PropertyDetailedResponse> getAllDetailedPropertiesByStatus(int page, int size, PropertyStatus status) {

        Pageable pageable = Pageable.ofSize(size).withPage(page);

        Page<PropertyEntity> property;

        // Filter properties by status when a status is provided
        if(status != null) {
            property = propertyRepository.findByStatus(status, pageable);
        } else {
            property = propertyRepository.findAll(pageable);
        }

        return property.map(propertyEntity -> propertyMapper.toPropertyDetailedResponse(propertyEntity));
    }

    // Get detailed property by its ID, throwing an exception if not found
    @Override
    public PropertyDetailedResponse getPropertyById(Long id) {

        PropertyEntity property = propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property"));

        return propertyMapper.toPropertyDetailedResponse(property);
    }

    // Create a new property, verifying the current user as the owner and handling images, amenities, and check-in slots
    @Override
    @Transactional
    public PropertyDetailedResponse createProperty(PropertyCreateRequest request){

        UserEntity user = ownershipVerifier.getCurrentUser();

        if (!user.getRole().equals(UserRole.HOST)) {
            throw new AccessDeniedException(
                    "You do not have permission to access this resource."
            );
        }

        PropertyEntity property = propertyMapper.toPropertyEntity(request, user);

        // Image
        Set<String> uniqueUrls = new HashSet<>();

        for(String imageUrl : request.getImageUrls()){
            uniqueUrls.add(imageUrl);
        }

        if (uniqueUrls.size() != request.getImageUrls().size()) {
            throw new DuplicateImageException();
        }

        List<ImageEntity> images = request.getImageUrls()
                .stream()
                .map(imageUrl -> ImageEntity.builder()
                        .imageUrl(imageUrl)
                        .property(property)
                        .build())
                .toList();

        property.setImages(images);

        // Amenity
        Set<Long> uniqueAmenityIds = new HashSet<>();

        for(Long amenityId : request.getAmenityIds()){
            uniqueAmenityIds.add(amenityId);
        }

        if (uniqueAmenityIds.size() != request.getAmenityIds().size()) {
            throw new DuplicateAmenityException();
        }

        List<AmenityEntity> amenities = amenityRepository.findAllById(request.getAmenityIds());

        if (amenities.size() != request.getAmenityIds().size()) {
            throw new ResourceNotFoundException("One or more amenities");
        }

        property.setAmenities(amenities);

        // Check In Slots
        List<CheckInSlotEntity> checkInSlots = request.getCheckInSlots()
                .stream()
                .map(slotRequest -> checkInSlotMapper.toCheckInSlotEntity(slotRequest, property))
                .toList();

        property.setCheckInSlots(checkInSlots);

        PropertyEntity savedProperty = propertyRepository.save(property);

        return propertyMapper.toPropertyDetailedResponse(savedProperty);
    }

    // Update a property by its ID, verifying ownership or admin rights before updating
    @Override
    @Transactional
    public PropertyDetailedResponse updateProperty(Long id, PropertyUpdateRequest update) {

        PropertyEntity property = propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property"));

        ownershipVerifier.verifyOwnershipOrAdmin(property.getUser());

        // Update fields if they are not null or blank.
        if (update.getTitle() != null && !update.getTitle().isBlank()) {
            property.setTitle(update.getTitle());
        }
        if (update.getDescription() != null && !update.getDescription().isBlank()) {
            property.setDescription(update.getDescription());
        }

        if (update.getBedrooms() != null) {
            property.setBedrooms(update.getBedrooms());
        }
        if (update.getBathrooms() != null) {
            property.setBathrooms(update.getBathrooms());
        }
        if (update.getMaxGuests() != null) {
            property.setMaxGuests(update.getMaxGuests());
        }
        if (update.getAddress() != null && !update.getAddress().isBlank()) {
            property.setAddress(update.getAddress());
        }

        // Image
        if (update.getImageUrls() != null) {

            // Check for duplicate image URLs before replacing the property's images
            Set<String> uniqueUrls = new HashSet<>();

            for(String url : update.getImageUrls()){
                uniqueUrls.add(url);
            }

            if (uniqueUrls.size() != update.getImageUrls().size()) {
                throw new DuplicateImageException();
            }

            // Convert image URLs into image entities linked to the property
            List<ImageEntity> images = update.getImageUrls()
                    .stream()
                    .map(imageUrl -> ImageEntity.builder()
                            .imageUrl(imageUrl)
                            .property(property)
                            .build())
                    .toList();

            // Mutate the existing Hibernate-managed collection instead of replacing it,
            // since 'images' has orphanRemoval = true
            property.getImages().clear();
            property.getImages().addAll(images);
        }

        // Amenity
        if (update.getAmenityIds() != null) {

            // Retrieve the requested amenities and verify that all IDs exist
            List<AmenityEntity> amenities = amenityRepository.findAllById(update.getAmenityIds());

            if (amenities.size() != update.getAmenityIds().size()) {
                throw new ResourceNotFoundException("One or more amenities");
            }

            property.setAmenities(amenities);
        }

        // Check-in Slots
        if (update.getCheckInSlots() != null) {

            // Convert slot requests into entities linked to the property
            List<CheckInSlotEntity> checkInSlots = update.getCheckInSlots()
                    .stream()
                    .map(slotRequest -> checkInSlotMapper.toCheckInSlotEntity(slotRequest, property))
                    .toList();

            // Mutate the existing Hibernate-managed collection instead of replacing it,
            // since 'checkInSlots' has orphanRemoval = true
            property.getCheckInSlots().clear();
            property.getCheckInSlots().addAll(checkInSlots);
        }

        PropertyEntity updatedProperty = propertyRepository.save(property);

        return propertyMapper.toPropertyDetailedResponse(updatedProperty);
    }
    // Delete a property by its ID, verifying ownership or admin rights before deletion
    @Override
    public void deleteProperty(Long id) {

        PropertyEntity property = propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property"));

        ownershipVerifier.verifyOwnershipOrAdmin(property.getUser());

        propertyRepository.delete(property);

    }
}