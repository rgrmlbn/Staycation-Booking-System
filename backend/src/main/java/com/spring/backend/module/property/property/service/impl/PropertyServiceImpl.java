package com.spring.backend.module.property.property.service.impl;

import com.spring.backend.exception.common.ResourceNotFoundException;
import com.spring.backend.exception.property.property.DuplicateAmenityException;
import com.spring.backend.exception.property.property.DuplicateImageException;
import com.spring.backend.exception.property.property.InvalidTimeRangeException;
import com.spring.backend.exception.property.property.OverlappingTimeSlotException;
import com.spring.backend.module.property.property.dto.request.CheckInSlotRequest;
import com.spring.backend.module.property.property.dto.request.PropertyCreateRequest;
import com.spring.backend.module.property.property.dto.request.PropertyUpdateRequest;
import com.spring.backend.module.property.property.dto.response.PropertyDetailedResponse;
import com.spring.backend.module.property.property.dto.response.PropertySummaryResponse;
import com.spring.backend.module.property.property.entity.AmenityEntity;
import com.spring.backend.module.property.property.entity.CheckInSlotEntity;
import com.spring.backend.module.property.property.entity.ImageEntity;
import com.spring.backend.module.property.property.entity.PropertyEntity;
import com.spring.backend.module.property.property.enums.PropertyStatus;
import com.spring.backend.module.property.property.mapper.PropertyMapper;
import com.spring.backend.module.property.property.repository.AmenityRepository;
import com.spring.backend.module.property.property.repository.PropertyRepository;
import com.spring.backend.module.property.property.service.interfaces.PropertyService;
import com.spring.backend.module.shared.util.OwnershipVerifier;
import com.spring.backend.module.user.user.entity.UserEntity;
import com.spring.backend.module.user.user.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.connection.RedisSubscribedConnectionException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.access.AccessDeniedException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PropertyServiceImpl implements PropertyService {

    private final PropertyRepository propertyRepository;
    private final PropertyMapper propertyMapper;
    private final AmenityRepository amenityRepository;
    private final OwnershipVerifier ownershipVerifier;


    // Get all properties owned by the current user, with optional title filtering and pagination support
    @Override
    public Page<PropertyDetailedResponse> getAllMyProperties(int page, int size, String title) {

        UserEntity user = ownershipVerifier.getCurrentUser();

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
                .orElseThrow(() -> new RedisSubscribedConnectionException("Property"));

        return propertyMapper.toPropertyDetailedResponse(property);
    }

    // MAIN HELPER: Validates that the check-in slots do not have zero length and do not overlap with each other
    private void validateCheckInSlots(List<CheckInSlotRequest> slots) {

        validateNoZeroLengthSlots(slots);
        validateNoOverlaps(slots);
    }

    // SUB-HELPER: Validates that no check-in slot has a zero length (start time equals end time)
    private void validateNoZeroLengthSlots(List<CheckInSlotRequest> slots) {

        for(CheckInSlotRequest slot : slots) {
            if (slot.getStartTime().equals(slot.getEndTime())) {
                throw new InvalidTimeRangeException();
            }
        }
    }

    // SUB-HELPER: Validates that no check-in slots overlap with each other
    private void validateNoOverlaps(List<CheckInSlotRequest> slots) {

        for (CheckInSlotRequest currentSlot : slots) {

            for (CheckInSlotRequest otherSlot : slots) {

                if (currentSlot == otherSlot) {
                    continue;
                }

                // Check whether the current slot overlaps with another slot
                if (slotsOverlap(currentSlot, otherSlot)) {
                    throw new OverlappingTimeSlotException();
                }
            }
        }
    }

    // VALIDATE NO OVERLAPS YESSUB-HELPER: Checks if two check-in slots overlap
    private boolean slotsOverlap(CheckInSlotRequest first, CheckInSlotRequest second) {
        return first.getStartTime().isBefore(second.getEndTime())
                && second.getStartTime().isBefore(first.getEndTime());
    }

    // Create a new property, verifying the current user as the owner and handling images, amenities, and check-in slots
    @Override
    @Transactional
    public PropertyDetailedResponse createProperty(PropertyCreateRequest request){

        UserEntity user = ownershipVerifier.getCurrentUser();

        PropertyEntity property = propertyMapper.toPropertyEntity(request, user);

        // Image
        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {

            // Check for duplicate image URLs before creating image entities
            Set<String> uniqueUrls = new HashSet<>();

            for (String url : request.getImageUrls()) {
                uniqueUrls.add(url);
            }

            if (uniqueUrls.size() != request.getImageUrls().size()) {
                throw new DuplicateImageException();
            }

            // Convert image URLs into image entities linked to the property
            List<ImageEntity> images = request.getImageUrls()
                    .stream()
                    .map(imageUrl -> ImageEntity.builder()
                            .imageUrl(imageUrl)
                            .property(property)
                            .build())
                    .toList();

            property.setImages(images);

        }

        // Amenity
        if (request.getAmenityIds() != null && !request.getAmenityIds().isEmpty()) {

            // Check for duplicate amenity IDs before retrieving amenities
            Set<Long> uniqueAmenityIds = new HashSet<>();

            for(Long amenityId : request.getAmenityIds()) {
                uniqueAmenityIds.add(amenityId);
            }

            if (uniqueAmenityIds.size() != request.getAmenityIds().size()) {
                throw new DuplicateAmenityException();
            }

            // Retrieve the requested amenities and verify that all IDs exist
            List<AmenityEntity> amenities = amenityRepository.findAllById(request.getAmenityIds());

            if (amenities.size() != request.getAmenityIds().size()) {
                throw new ResourceNotFoundException("One or more amenities");
            }

            property.setAmenities(amenities);
        }

        // Check-in Slots
        if (request.getCheckInSlots() != null && !request.getCheckInSlots().isEmpty()) {

            // Validate slot time ranges and prevent overlapping slots
            validateCheckInSlots(request.getCheckInSlots());

            // Convert slot requests into entities linked to the property
            List<CheckInSlotEntity> checkInSlots = request.getCheckInSlots()
                    .stream()
                    .map(slotRequest -> propertyMapper.toCheckInSlotEntity(slotRequest, property))
                    .toList();

            property.setCheckInSlots(checkInSlots);
        }

        PropertyEntity savedProperty = propertyRepository.save(property);

        return propertyMapper.toPropertyDetailedResponse(savedProperty);
    }

    // Update a property by its ID, verifying ownership or admin rights before updating
    @Override
    @Transactional
    public PropertyDetailedResponse updateProperty(Long id, PropertyUpdateRequest update) {

        PropertyEntity property = propertyRepository.findById(id)
                .orElseThrow(() -> new RedisSubscribedConnectionException("Property"));

        ownershipVerifier.verifyOwnershipOrAdmin(property.getUser());

        // Update fields if they are not null or blank
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

        if(update.getMaxGuests() != null) {
            property.setMaxGuests(update.getMaxGuests());
        }

        if(update.getAddress() != null && !update.getAddress().isBlank()) {
            property.setAddress(update.getAddress());
        }

        // Image
        if (update.getImageUrls() != null && !update.getImageUrls().isEmpty()) {

            // Check for duplicate image URLs before replacing the property's images
            Set<String> uniqueUrls = new HashSet<>();

            for(String imageUrl : update.getImageUrls()) {
                uniqueUrls.add(imageUrl);
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

            property.setImages(images);
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
        if (update.getCheckInSlots() != null && !update.getCheckInSlots().isEmpty()) {

            // Validate slot time ranges and prevent overlapping slots
            validateCheckInSlots(update.getCheckInSlots());

            // Convert slot requests into entities linked to the property
            List<CheckInSlotEntity> checkInSlots = update.getCheckInSlots()
                    .stream()
                    .map(slotRequest -> propertyMapper.toCheckInSlotEntity(slotRequest, property))
                    .toList();

            property.setCheckInSlots(checkInSlots);
        }

        PropertyEntity updatedProperty = propertyRepository.save(property);

        return propertyMapper.toPropertyDetailedResponse(updatedProperty);
    }

    // Delete a property by its ID, verifying ownership or admin rights before deletion
    @Override
    public void deleteProperty(Long id) {

        PropertyEntity property = propertyRepository.findById(id)
                .orElseThrow(() -> new RedisSubscribedConnectionException("Property"));

        ownershipVerifier.verifyOwnershipOrAdmin(property.getUser());

        propertyRepository.delete(property);

    }
}