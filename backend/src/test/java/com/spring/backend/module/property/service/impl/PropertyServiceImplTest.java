package com.spring.backend.module.property.service.impl;

import com.spring.backend.module.property.property.dto.request.PropertyCreateRequest;
import com.spring.backend.module.property.property.dto.request.PropertyUpdateRequest;
import com.spring.backend.module.property.property.dto.response.PropertyDetailedResponse;
import com.spring.backend.module.property.property.dto.response.PropertySummaryResponse;
import com.spring.backend.module.property.property.entity.AmenityEntity;
import com.spring.backend.module.property.property.entity.PropertyEntity;
import com.spring.backend.module.property.property.enums.PropertyStatus;
import com.spring.backend.module.property.property.mapper.PropertyMapper;
import com.spring.backend.module.property.property.repository.AmenityRepository;
import com.spring.backend.module.property.property.repository.PropertyRepository;
import com.spring.backend.module.property.property.service.impl.PropertyServiceImpl;
import com.spring.backend.module.shared.util.OwnershipVerifier;
import com.spring.backend.module.user.user.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.connection.RedisSubscribedConnectionException;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PropertyServiceImplTest {

    @Mock
    private PropertyRepository propertyRepository; // Fake repository — no real database involved

    @Mock
    private PropertyMapper propertyMapper; // Fake mapper — controls exactly what DTO/entity conversion returns

    @Mock
    private AmenityRepository amenityRepository; // Fake amenity repository — controls which amenities "exist"

    @Mock
    private OwnershipVerifier ownershipVerifier; // Fake ownership check — lets us simulate allowed/denied access

    @InjectMocks
    private PropertyServiceImpl propertyService; // Real service, wired with all mocks above

    private PropertyEntity property;
    private UserEntity owner;

    @BeforeEach
    void setUp() {
        owner = new UserEntity();
        owner.setId(10L);

        property = new PropertyEntity();
        property.setId(1L);
        property.setTitle("Cozy Cabin");
        property.setDescription("A quiet retreat in the woods.");
        property.setAddress("123 Forest Road");
        property.setUser(owner);
    }

    // ---------- getAllSummaryProperties() ----------

    @Test
    @DisplayName("Should filter by title and return a mapped page when a title is provided")
    void getAllSummaryProperties_titleProvided_returnsFilteredMappedPage() {
        Pageable expectedPageable = Pageable.ofSize(10).withPage(0);
        Page<PropertyEntity> entityPage = new PageImpl<>(List.of(property), expectedPageable, 1);
        PropertySummaryResponse response = mock(PropertySummaryResponse.class);

        when(propertyRepository.findByTitleContainingIgnoreCase(eq("Cabin"), eq(expectedPageable)))
                .thenReturn(entityPage); // Simulate the repository filtering by title
        when(propertyMapper.toSummaryResponse(property)).thenReturn(response);

        Page<PropertySummaryResponse> result = propertyService.getAllSummaryProperties(0, 10, "Cabin");

        assertThat(result.getContent()).containsExactly(response); // Confirm it's the mapped DTO, not the raw entity
        verify(propertyRepository, never()).findAll(any(Pageable.class)); // Confirm the unfiltered query was never used
    }

    @Test
    @DisplayName("Should return all properties mapped when no title is provided")
    void getAllSummaryProperties_noTitle_returnsAllMappedPage() {
        Pageable expectedPageable = Pageable.ofSize(10).withPage(0);
        Page<PropertyEntity> entityPage = new PageImpl<>(List.of(property), expectedPageable, 1);
        PropertySummaryResponse response = mock(PropertySummaryResponse.class);

        when(propertyRepository.findAll(expectedPageable)).thenReturn(entityPage); // Simulate an unfiltered page of results
        when(propertyMapper.toSummaryResponse(property)).thenReturn(response);

        Page<PropertySummaryResponse> result = propertyService.getAllSummaryProperties(0, 10, null);

        assertThat(result.getContent()).containsExactly(response);
        verify(propertyRepository, never()).findByTitleContainingIgnoreCase(any(), any()); // Confirm no title filter was applied
    }

    @Test
    @DisplayName("Should return all properties mapped when the title is blank")
    void getAllSummaryProperties_blankTitle_returnsAllMappedPage() {
        Pageable expectedPageable = Pageable.ofSize(10).withPage(0);
        Page<PropertyEntity> entityPage = new PageImpl<>(List.of(property), expectedPageable, 1);

        when(propertyRepository.findAll(expectedPageable)).thenReturn(entityPage);
        when(propertyMapper.toSummaryResponse(property)).thenReturn(mock(PropertySummaryResponse.class));

        propertyService.getAllSummaryProperties(0, 10, "   ");

        verify(propertyRepository, never()).findByTitleContainingIgnoreCase(any(), any()); // Confirm a blank title is treated like no title
    }

    // ---------- getAllDetailedProperties() ----------

    @Test
    @DisplayName("Should filter by title and return a detailed mapped page when a title is provided")
    void getAllDetailedProperties_titleProvided_returnsFilteredMappedPage() {
        Pageable expectedPageable = Pageable.ofSize(5).withPage(1);
        Page<PropertyEntity> entityPage = new PageImpl<>(List.of(property), expectedPageable, 1);
        PropertyDetailedResponse response = mock(PropertyDetailedResponse.class);

        when(propertyRepository.findByTitleContainingIgnoreCase(eq("Cabin"), eq(expectedPageable)))
                .thenReturn(entityPage);
        when(propertyMapper.toDetailedResponse(property)).thenReturn(response);

        Page<PropertyDetailedResponse> result = propertyService.getAllDetailedProperties(1, 5, "Cabin");

        assertThat(result.getContent()).containsExactly(response);
    }

    @Test
    @DisplayName("Should return all properties mapped in detail when no title is provided")
    void getAllDetailedProperties_noTitle_returnsAllMappedPage() {
        Pageable expectedPageable = Pageable.ofSize(5).withPage(0);
        Page<PropertyEntity> entityPage = new PageImpl<>(List.of(property), expectedPageable, 1);
        PropertyDetailedResponse response = mock(PropertyDetailedResponse.class);

        when(propertyRepository.findAll(expectedPageable)).thenReturn(entityPage);
        when(propertyMapper.toDetailedResponse(property)).thenReturn(response);

        Page<PropertyDetailedResponse> result = propertyService.getAllDetailedProperties(0, 5, null);

        assertThat(result.getContent()).containsExactly(response);
    }

    // ---------- getAllDetailedPropertiesByStatus() ----------

    @Test
    @DisplayName("Should filter by status and return a detailed mapped page when a status is provided")
    void getAllDetailedPropertiesByStatus_statusProvided_returnsFilteredMappedPage() {
        Pageable expectedPageable = Pageable.ofSize(10).withPage(0);
        Page<PropertyEntity> entityPage = new PageImpl<>(List.of(property), expectedPageable, 1);
        PropertyDetailedResponse response = mock(PropertyDetailedResponse.class);

        when(propertyRepository.findByStatus(eq(PropertyStatus.AVAILABLE), eq(expectedPageable)))
                .thenReturn(entityPage); // Simulate the repository filtering by status
        when(propertyMapper.toDetailedResponse(property)).thenReturn(response);

        Page<PropertyDetailedResponse> result =
                propertyService.getAllDetailedPropertiesByStatus(0, 10, PropertyStatus.AVAILABLE);

        assertThat(result.getContent()).containsExactly(response);
        verify(propertyRepository, never()).findAll(any(Pageable.class)); // Confirm the unfiltered query was never used
    }

    @Test
    @DisplayName("Should return all properties mapped in detail when no status is provided")
    void getAllDetailedPropertiesByStatus_noStatus_returnsAllMappedPage() {
        Pageable expectedPageable = Pageable.ofSize(10).withPage(0);
        Page<PropertyEntity> entityPage = new PageImpl<>(List.of(property), expectedPageable, 1);

        when(propertyRepository.findAll(expectedPageable)).thenReturn(entityPage);
        when(propertyMapper.toDetailedResponse(property)).thenReturn(mock(PropertyDetailedResponse.class));

        propertyService.getAllDetailedPropertiesByStatus(0, 10, null);

        verify(propertyRepository, never()).findByStatus(any(), any()); // Confirm no status filter was applied
    }

    // ---------- getPropertyById() ----------

    @Test
    @DisplayName("Should return property when ID exists")
    void getPropertyById_propertyExists_returnsMappedProperty() {
        PropertyDetailedResponse response = mock(PropertyDetailedResponse.class);

        when(propertyRepository.findById(1L)).thenReturn(Optional.of(property)); // Simulate finding the property
        when(propertyMapper.toDetailedResponse(property)).thenReturn(response);

        PropertyDetailedResponse result = propertyService.getPropertyById(1L);

        assertThat(result).isEqualTo(response); // Confirm the returned DTO matches what the mapper produced
    }

    @Test
    @DisplayName("Should throw RedisSubscribedConnectionException when property ID does not exist")
    void getPropertyById_propertyDoesNotExist_throws() {
        when(propertyRepository.findById(1L)).thenReturn(Optional.empty()); // Simulate no property found

        assertThatThrownBy(() -> propertyService.getPropertyById(1L))
                .isInstanceOf(RedisSubscribedConnectionException.class); // Matches the exception currently thrown by the service

        verify(propertyMapper, never()).toDetailedResponse(any()); // Confirm mapping was never attempted since there was nothing to map
    }

    // ---------- createProperty() ----------

    @Test
    @DisplayName("Should map and save the property, returning the mapped response")
    void createProperty_mapsAndReturnsResponse() {
        PropertyCreateRequest request = mock(PropertyCreateRequest.class);
        UserEntity user = mock(UserEntity.class);
        PropertyEntity mappedEntity = new PropertyEntity();
        PropertyEntity savedEntity = new PropertyEntity();
        PropertyDetailedResponse response = mock(PropertyDetailedResponse.class);

        when(request.getAmenityIds()).thenReturn(null); // or Collections.emptyList(), to skip the amenities branch
        when(ownershipVerifier.getCurrentUser()).thenReturn(user);
        when(propertyMapper.toPropertyEntity(request, user)).thenReturn(mappedEntity);
        when(propertyRepository.save(mappedEntity)).thenReturn(savedEntity);
        when(propertyMapper.toDetailedResponse(savedEntity)).thenReturn(response);

        PropertyDetailedResponse result = propertyService.createProperty(request);

        assertThat(result).isEqualTo(response);
        verify(propertyRepository).save(mappedEntity);
    }
    // ---------- updateProperty() ----------

    @Test
    @DisplayName("Should update all scalar fields when provided and valid")
    void updateProperty_allScalarFieldsProvided_updatesAllFields() {
        PropertyUpdateRequest update = mock(PropertyUpdateRequest.class);
        PropertyDetailedResponse response = mock(PropertyDetailedResponse.class);

        when(update.getTitle()).thenReturn("Updated Title");
        when(update.getDescription()).thenReturn("Updated Description");
        when(update.getPricePerNight()).thenReturn(150.0);
        when(update.getBedrooms()).thenReturn(3);
        when(update.getBathrooms()).thenReturn(2);
        when(update.getAirConditioning()).thenReturn(true);
        when(update.getAddress()).thenReturn("456 Updated Ave");
        when(update.getImageUrls()).thenReturn(null);
        when(update.getAmenityIds()).thenReturn(null);

        when(propertyRepository.findById(1L)).thenReturn(Optional.of(property)); // Simulate finding the existing property
        when(propertyRepository.save(property)).thenReturn(property); // Simulate persisting the updated entity
        when(propertyMapper.toDetailedResponse(property)).thenReturn(response);

        PropertyDetailedResponse result = propertyService.updateProperty(1L, update);

        assertThat(property.getTitle()).isEqualTo("Updated Title"); // Confirm each field was actually changed
        assertThat(property.getDescription()).isEqualTo("Updated Description");
        assertThat(property.getPricePerNight()).isEqualTo(150.0);
        assertThat(property.getBedrooms()).isEqualTo(3);
        assertThat(property.getBathrooms()).isEqualTo(2);
        assertThat(property.getAirConditioning()).isTrue();
        assertThat(property.getAddress()).isEqualTo("456 Updated Ave");
        assertThat(result).isEqualTo(response);
        verify(ownershipVerifier).verifyOwnershipOrAdmin(owner); // Confirm the caller's ownership/admin status was checked
        verify(propertyRepository).save(property); // Confirm the updated entity was persisted
    }

    @Test
    @DisplayName("Should leave text fields unchanged when update values are null or blank")
    void updateProperty_blankOrNullTextFields_keepsOriginalValues() {
        PropertyUpdateRequest update = mock(PropertyUpdateRequest.class);

        when(update.getTitle()).thenReturn("   "); // Blank, should be ignored
        when(update.getDescription()).thenReturn(null);
        when(update.getAddress()).thenReturn("   "); // Blank, should be ignored

        when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));
        when(propertyRepository.save(property)).thenReturn(property);
        when(propertyMapper.toDetailedResponse(property)).thenReturn(mock(PropertyDetailedResponse.class));

        propertyService.updateProperty(1L, update);

        assertThat(property.getTitle()).isEqualTo("Cozy Cabin"); // Confirm nothing changed
        assertThat(property.getDescription()).isEqualTo("A quiet retreat in the woods.");
        assertThat(property.getAddress()).isEqualTo("123 Forest Road");
    }

    @Test
    @DisplayName("Should rebuild the image list when image URLs are provided")
    void updateProperty_imageUrlsProvided_replacesImages() {
        PropertyUpdateRequest update = mock(PropertyUpdateRequest.class);

        when(update.getImageUrls()).thenReturn(List.of("https://example.com/a.jpg", "https://example.com/b.jpg"));

        when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));
        when(propertyRepository.save(property)).thenReturn(property);
        when(propertyMapper.toDetailedResponse(property)).thenReturn(mock(PropertyDetailedResponse.class));

        propertyService.updateProperty(1L, update);

        assertThat(property.getImages()).hasSize(2); // Confirm both image URLs were converted into entities
        assertThat(property.getImages())
                .extracting("imageUrl")
                .containsExactly("https://example.com/a.jpg", "https://example.com/b.jpg");
        assertThat(property.getImages())
                .allMatch(image -> image.getProperty() == property); // Confirm each image is linked back to this property
    }

    @Test
    @DisplayName("Should replace amenities when amenity IDs are provided")
    void updateProperty_amenityIdsProvided_replacesAmenities() {
        PropertyUpdateRequest update = mock(PropertyUpdateRequest.class);
        AmenityEntity amenity = new AmenityEntity();
        amenity.setId(2L);
        amenity.setName("Wi-Fi");

        when(update.getAmenityIds()).thenReturn(List.of(2L));

        when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));
        when(amenityRepository.findAllById(List.of(2L))).thenReturn(List.of(amenity)); // Simulate resolving amenity IDs into entities
        when(propertyRepository.save(property)).thenReturn(property);
        when(propertyMapper.toDetailedResponse(property)).thenReturn(mock(PropertyDetailedResponse.class));

        propertyService.updateProperty(1L, update);

        assertThat(property.getAmenities()).containsExactly(amenity); // Confirm the resolved amenities were attached
    }

    @Test
    @DisplayName("Should throw RedisSubscribedConnectionException when updating a property that does not exist")
    void updateProperty_propertyDoesNotExist_throws() {
        PropertyUpdateRequest update = mock(PropertyUpdateRequest.class);

        when(propertyRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> propertyService.updateProperty(1L, update))
                .isInstanceOf(RedisSubscribedConnectionException.class);

        verify(ownershipVerifier, never()).verifyOwnershipOrAdmin(any()); // Confirm ownership was never checked since there was no property
        verify(propertyRepository, never()).save(any()); // Confirm nothing was saved since the property doesn't exist
    }

    @Test
    @DisplayName("Should propagate the exception and skip saving when ownership verification fails")
    void updateProperty_notOwnerOrAdmin_doesNotSave() {
        PropertyUpdateRequest update = mock(PropertyUpdateRequest.class);

        when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));
        doThrow(new RuntimeException("Forbidden")) // Simulate the ownership check rejecting the caller
                .when(ownershipVerifier).verifyOwnershipOrAdmin(owner);

        assertThatThrownBy(() -> propertyService.updateProperty(1L, update))
                .isInstanceOf(RuntimeException.class);

        verify(propertyRepository, never()).save(any()); // Confirm nothing was saved once ownership was rejected
    }

    // ---------- deleteProperty() ----------

    @Test
    @DisplayName("Should delete property when ID exists")
    void deleteProperty_propertyExists_deletesProperty() {
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));

        propertyService.deleteProperty(1L);

        verify(ownershipVerifier).verifyOwnershipOrAdmin(owner); // Confirm the caller's ownership/admin status was checked
        verify(propertyRepository).delete(property); // Confirm the correct entity was passed to delete
    }

    @Test
    @DisplayName("Should throw RedisSubscribedConnectionException when deleting a property that does not exist")
    void deleteProperty_propertyDoesNotExist_throws() {
        when(propertyRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> propertyService.deleteProperty(1L))
                .isInstanceOf(RedisSubscribedConnectionException.class);

        verify(ownershipVerifier, never()).verifyOwnershipOrAdmin(any()); // Confirm ownership was never checked since there was no property
        verify(propertyRepository, never()).delete(any()); // Confirm delete was never attempted
    }

    @Test
    @DisplayName("Should propagate the exception and skip deleting when ownership verification fails")
    void deleteProperty_notOwnerOrAdmin_doesNotDelete() {
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));
        doThrow(new AccessDeniedException("Access Denied")) // Simulate the ownership check rejecting the caller
                .when(ownershipVerifier).verifyOwnershipOrAdmin(owner);

        assertThatThrownBy(() -> propertyService.deleteProperty(1L))
                .isInstanceOf(AccessDeniedException.class);

        verify(propertyRepository, never()).delete(any()); // Confirm nothing was deleted once ownership was rejected
    }
}