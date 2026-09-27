package com.spring.backend.module.property.amenity.service.impl;

import com.spring.backend.exception.common.ResourceNotFoundException;
import com.spring.backend.exception.property.property.DuplicateAmenityException;
import com.spring.backend.module.property.amenity.dto.request.AmenityCreateRequest;
import com.spring.backend.module.property.amenity.dto.request.AmenityUpdateRequest;
import com.spring.backend.module.property.amenity.dto.response.AmenityResponse;
import com.spring.backend.module.property.amenity.entity.AmenityEntity;
import com.spring.backend.module.property.amenity.mapper.AmenityMapper;
import com.spring.backend.module.property.amenity.repository.AmenityRepository;
import com.spring.backend.module.shared.util.OwnershipVerifier;
import com.spring.backend.module.property.amenity.service.impl.AmenityServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AmenityServiceImplTest {

    @Mock
    private AmenityRepository amenityRepository; // Fake repository — no real database involved

    @Mock
    private AmenityMapper amenityMapper; // Fake mapper — controls exactly what DTO conversion returns

    @Mock
    private OwnershipVerifier ownershipVerifier;

    @InjectMocks
    private AmenityServiceImpl amenityService; // Real service, wired with the two mocks above

    private AmenityEntity amenity;

    @BeforeEach
    void setUp() {
        amenity = new AmenityEntity();
        amenity.setId(1L);
        amenity.setName("Wi-Fi");
    }

    // ---------- getAllAmenities() ----------

    @Test
    @DisplayName("Should return a mapped list of all amenities")
    void getAllAmenities_returnsMappedList() {
        AmenityResponse response = mock(AmenityResponse.class);

        when(amenityRepository.findAll()).thenReturn(List.of(amenity)); // Simulate one amenity existing in the "database"
        when(amenityMapper.toAmenityResponse(amenity)).thenReturn(response); // Simulate the mapper converting it to a DTO

        List<AmenityResponse> result = amenityService.getAllAmenities();

        assertThat(result).hasSize(1); // Confirm the list has exactly the one amenity
        assertThat(result.get(0)).isEqualTo(response); // Confirm it's the mapped DTO, not the raw entity
        verify(ownershipVerifier).verifyAdmin();
    }

    @Test
    @DisplayName("Should return an empty list when there are no amenities")
    void getAllAmenities_noAmenities_returnsEmptyList() {
        when(amenityRepository.findAll()).thenReturn(List.of()); // Simulate an empty database

        List<AmenityResponse> result = amenityService.getAllAmenities();

        assertThat(result).isEmpty(); // Confirm no amenities means no results, not a null or an exception
        verify(ownershipVerifier).verifyAdmin();
    }

    // ---------- getAmenityById() ----------

    @Test
    @DisplayName("Should return amenity when ID exists")
    void getAmenityById_amenityExists_returnsMappedAmenity() {
        AmenityResponse response = mock(AmenityResponse.class);

        when(amenityRepository.findById(1L)).thenReturn(Optional.of(amenity)); // Simulate finding the amenity
        when(amenityMapper.toAmenityResponse(amenity)).thenReturn(response); // Simulate mapping it to a DTO

        AmenityResponse result = amenityService.getAmenityById(1L);

        assertThat(result).isEqualTo(response); // Confirm the returned DTO matches what the mapper produced
        verify(ownershipVerifier).verifyAdmin();
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when amenity ID does not exist")
    void getAmenityById_amenityDoesNotExist_throws() {
        when(amenityRepository.findById(1L)).thenReturn(Optional.empty()); // Simulate no amenity found

        assertThatThrownBy(() -> amenityService.getAmenityById(1L))
                .isInstanceOf(ResourceNotFoundException.class); // Expect the service to reject a missing ID

        verify(amenityMapper, never()).toAmenityResponse(any()); // Confirm mapping was never attempted since there was nothing to map
    }

    // ---------- createAmenity() ----------

    @Test
    @DisplayName("Should map, save, and return the created amenity")
    void createAmenity_savesAndReturnsMappedAmenity() {
        AmenityCreateRequest request = mock(AmenityCreateRequest.class); // Fake incoming request
        AmenityEntity mappedEntity = new AmenityEntity(); // What the mapper produces from the request
        AmenityEntity savedEntity = new AmenityEntity(); // What the repository "returns" after saving (e.g. with a generated ID)
        AmenityResponse response = mock(AmenityResponse.class);

        when(amenityMapper.toAmenityEntity(request)).thenReturn(mappedEntity); // Simulate converting the request DTO into an entity
        when(amenityRepository.save(mappedEntity)).thenReturn(savedEntity); // Simulate persisting it
        when(amenityMapper.toAmenityResponse(savedEntity)).thenReturn(response); // Simulate converting the saved entity back into a response DTO

        AmenityResponse result = amenityService.createAmenity(request);

        assertThat(result).isEqualTo(response); // Confirm the final result is the mapped response
        verify(amenityRepository).save(mappedEntity); // Confirm the mapped entity was actually persisted
        verify(ownershipVerifier).verifyAdmin();
    }

    @Test
    @DisplayName("Should reject creating an amenity with an existing name")
    void createAmenity_duplicateName_throws() {
        AmenityCreateRequest request = mock(AmenityCreateRequest.class);
        when(request.getName()).thenReturn("Wi-Fi");
        when(amenityRepository.existsByName("Wi-Fi")).thenReturn(true);

        assertThatThrownBy(() -> amenityService.createAmenity(request))
                .isInstanceOf(DuplicateAmenityException.class);

        verify(ownershipVerifier, never()).verifyAdmin();
        verify(amenityRepository, never()).save(any());
    }

    // ---------- updateAmenity() ----------

    @Test
    @DisplayName("Should update name when a valid name is provided")
    void updateAmenity_nameProvided_updatesName() {
        AmenityUpdateRequest update = mock(AmenityUpdateRequest.class);
        AmenityResponse response = mock(AmenityResponse.class);

        when(update.getName()).thenReturn("Swimming Pool");
        when(amenityRepository.findById(1L)).thenReturn(Optional.of(amenity)); // Simulate finding the existing amenity
        when(amenityRepository.save(amenity)).thenReturn(amenity); // Simulate persisting the updated entity
        when(amenityMapper.toAmenityResponse(amenity)).thenReturn(response);

        AmenityResponse result = amenityService.updateAmenity(1L, update);

        assertThat(amenity.getName()).isEqualTo("Swimming Pool"); // Confirm the entity's name was actually changed
        assertThat(result).isEqualTo(response);
        verify(amenityRepository).save(amenity); // Confirm the updated entity was persisted
        verify(ownershipVerifier).verifyAdmin();
    }

    @Test
    @DisplayName("Should leave name unchanged when update name is blank")
    void updateAmenity_blankName_keepsOriginalName() {
        AmenityUpdateRequest update = mock(AmenityUpdateRequest.class);

        when(update.getName()).thenReturn("   "); // Blank, should be ignored
        when(amenityRepository.findById(1L)).thenReturn(Optional.of(amenity));
        when(amenityRepository.save(amenity)).thenReturn(amenity); // Simulate persisting the (unchanged) entity
        when(amenityMapper.toAmenityResponse(amenity)).thenReturn(mock(AmenityResponse.class));

        amenityService.updateAmenity(1L, update);

        assertThat(amenity.getName()).isEqualTo("Wi-Fi"); // Confirm the original name was preserved, not overwritten with blank
        verify(ownershipVerifier).verifyAdmin();
    }

    @Test
    @DisplayName("Should leave name unchanged when update name is null")
    void updateAmenity_nullName_keepsOriginalName() {
        AmenityUpdateRequest update = mock(AmenityUpdateRequest.class);

        when(update.getName()).thenReturn(null);
        when(amenityRepository.findById(1L)).thenReturn(Optional.of(amenity));
        when(amenityRepository.save(amenity)).thenReturn(amenity); // Simulate persisting the (unchanged) entity
        when(amenityMapper.toAmenityResponse(amenity)).thenReturn(mock(AmenityResponse.class));

        amenityService.updateAmenity(1L, update);

        assertThat(amenity.getName()).isEqualTo("Wi-Fi"); // Nothing should change
        verify(amenityRepository).save(amenity); // Save still happens even if nothing changed, matching current service behavior
        verify(ownershipVerifier).verifyAdmin();
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating an amenity that does not exist")
    void updateAmenity_amenityDoesNotExist_throws() {
        AmenityUpdateRequest update = mock(AmenityUpdateRequest.class);

        when(amenityRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> amenityService.updateAmenity(1L, update))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(amenityRepository, never()).save(any()); // Confirm nothing was saved since the amenity doesn't exist
    }

    // ---------- deleteAmenity() ----------

    @Test
    @DisplayName("Should delete amenity when ID exists")
    void deleteAmenity_amenityExists_deletesAmenity() {
        when(amenityRepository.findById(1L)).thenReturn(Optional.of(amenity));

        amenityService.deleteAmenity(1L);

        verify(amenityRepository).delete(amenity); // Confirm the correct entity was passed to delete
        verify(ownershipVerifier).verifyAdmin();
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting an amenity that does not exist")
    void deleteAmenity_amenityDoesNotExist_throws() {
        when(amenityRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> amenityService.deleteAmenity(1L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(amenityRepository, never()).delete(any()); // Confirm delete was never attempted
    }
}