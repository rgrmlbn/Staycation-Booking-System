package com.spring.backend.module.property.property.service.impl;

import com.spring.backend.exception.common.ResourceNotFoundException;
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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.connection.RedisSubscribedConnectionException;
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
    @Transactional
    public PropertyDetailedResponse createProperty(PropertyCreateRequest request){

        UserEntity user = ownershipVerifier.getCurrentUser();

        PropertyEntity property = propertyMapper.toPropertyEntity(request, user);

        // Image
        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {

            Set<String> uniqueUrls = new HashSet<>();

            for (String url : request.getImageUrls()) {
                uniqueUrls.add(url);
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

        }

        // Amenity
        if (request.getAmenityIds() != null && !request.getAmenityIds().isEmpty()) {

            List<AmenityEntity> amenities = amenityRepository.findAllById(request.getAmenityIds());

            if (amenities.size() != request.getAmenityIds().size()) {
                throw new ResourceNotFoundException("One or more amenities");
            }

            property.setAmenities(amenities);
        }

        // Check-in Slots
        if (request.getCheckInSlots() != null && !request.getCheckInSlots().isEmpty()) {

            validateCheckInSlots(request.getCheckInSlots());

            List<CheckInSlotEntity> checkInSlots = request.getCheckInSlots()
                    .stream()
                    .map(slotRequest -> propertyMapper.toCheckInSlotEntity(slotRequest, property))
                    .toList();

            property.setCheckInSlots(checkInSlots);
        }

        PropertyEntity savedProperty = propertyRepository.save(property);

        return propertyMapper.toDetailedResponse(savedProperty);
    }

    private void validateCheckInSlots(List<CheckInSlotRequest> slots) {

        validateNoZeroLengthSlots(slots);
        validateNoOverlaps(slots);
    }

    private void validateNoZeroLengthSlots(List<CheckInSlotRequest> slots) {

        for(CheckInSlotRequest slot : slots) {
            if (slot.getStartTime().equals(slot.getEndTime())) {
                throw new InvalidTimeRangeException();
            }
        }
    }

    private void validateNoOverlaps(List<CheckInSlotRequest> slots) {

        for (CheckInSlotRequest currentSlot : slots) {

            for (CheckInSlotRequest otherSlot : slots) {

                if (currentSlot == otherSlot) {
                    continue;
                }

                if (slotsOverlap(currentSlot, otherSlot)) {
                    throw new OverlappingTimeSlotException();
                }
            }
        }
    }

    private boolean slotsOverlap(CheckInSlotRequest first, CheckInSlotRequest second) {
        return first.getStartTime().isBefore(second.getEndTime())
                && second.getStartTime().isBefore(first.getEndTime());
    }

    @Override
    @Transactional
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

        // Image
        if (update.getImageUrls() != null && !update.getImageUrls().isEmpty()) {

            Set<String> uniqueUrls = new HashSet<>(update.getImageUrls());

            if (uniqueUrls.size() != update.getImageUrls().size()) {
                throw new DuplicateImageException();
            }

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

            List<AmenityEntity> amenities = amenityRepository.findAllById(update.getAmenityIds());

            if (amenities.size() != update.getAmenityIds().size()) {
                throw new ResourceNotFoundException("One or more amenities");
            }

            property.setAmenities(amenities);
        }

        // Check-in Slots
        if (update.getCheckInSlots() != null && !update.getCheckInSlots().isEmpty()) {

            validateCheckInSlots(update.getCheckInSlots());

            List<CheckInSlotEntity> checkInSlots = update.getCheckInSlots()
                    .stream()
                    .map(slotRequest -> propertyMapper.toCheckInSlotEntity(slotRequest, property))
                    .toList();

            property.setCheckInSlots(checkInSlots);
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
