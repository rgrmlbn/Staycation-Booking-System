package com.spring.backend.module.property.booking.service.impl;

import com.spring.backend.exception.property.booking.BookingAlreadyExistsException;
import com.spring.backend.exception.property.booking.GuestCapacityExceededException;
import com.spring.backend.module.property.booking.dto.request.BookingCreateRequest;
import com.spring.backend.module.property.booking.dto.request.BookingUpdateRequest;
import com.spring.backend.module.property.booking.dto.response.BookingResponse;
import com.spring.backend.module.property.booking.entity.BookingEntity;
import com.spring.backend.module.property.booking.enums.BookingStatus;
import com.spring.backend.module.property.booking.mapper.BookingMapper;
import com.spring.backend.module.property.booking.repository.BookingRepository;
import com.spring.backend.module.property.checkin.entity.CheckInSlotEntity;
import com.spring.backend.module.property.checkin.repository.CheckInSlotRepository;
import com.spring.backend.module.property.property.entity.PropertyEntity;
import com.spring.backend.module.property.property.repository.PropertyRepository;
import com.spring.backend.module.shared.util.OwnershipVerifier;
import com.spring.backend.module.user.user.entity.UserEntity;
import com.spring.backend.module.user.user.enums.UserRole;
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
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private OwnershipVerifier ownershipVerifier;

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private CheckInSlotRepository checkInSlotRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private UserEntity guest;
    private UserEntity host;
    private PropertyEntity property;
    private CheckInSlotEntity slot;
    private BookingEntity booking;

    @BeforeEach
    void setUp() {
        guest = UserEntity.builder().id(1L).role(UserRole.GUEST).build();
        host = UserEntity.builder().id(2L).role(UserRole.HOST).build();
        property = PropertyEntity.builder().id(10L).user(host).maxGuests(4).build();
        slot = CheckInSlotEntity.builder()
                .id(20L)
                .property(property)
                .startTime(LocalTime.of(15, 0))
                .durationHours(24)
                .build();
        booking = BookingEntity.builder()
                .id(30L)
                .property(property)
                .guest(guest)
                .checkInSlot(slot)
                .checkInDateTime(LocalDateTime.of(2026, 10, 1, 15, 0))
                .checkOutDateTime(LocalDateTime.of(2026, 10, 2, 15, 0))
                .numberOfGuests(2)
                .status(BookingStatus.PENDING)
                .build();
    }

    @Test
    @DisplayName("Creates a guest booking with its calculated stay interval")
    void createBooking_validRequest_savesCalculatedBooking() {
        BookingCreateRequest request = mock(BookingCreateRequest.class);
        BookingResponse response = mock(BookingResponse.class);

        when(request.getPropertyId()).thenReturn(property.getId());
        when(request.getCheckInSlotId()).thenReturn(slot.getId());
        when(request.getCheckInDate()).thenReturn(LocalDate.of(2026, 10, 1));
        when(request.getNumberOfGuests()).thenReturn(2);
        when(ownershipVerifier.getCurrentUser()).thenReturn(guest);
        when(propertyRepository.findById(property.getId())).thenReturn(Optional.of(property));
        when(checkInSlotRepository.findById(slot.getId())).thenReturn(Optional.of(slot));
        when(bookingRepository.findByPropertyId(property.getId())).thenReturn(List.of());
        when(bookingMapper.toBookingEntity(request, property, guest, slot)).thenReturn(booking);
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.toBookingResponse(booking)).thenReturn(response);

        BookingResponse result = bookingService.createBooking(request);

        assertThat(result).isSameAs(response);
        assertThat(booking.getCheckInDateTime()).isEqualTo(LocalDateTime.of(2026, 10, 1, 15, 0));
        assertThat(booking.getCheckOutDateTime()).isEqualTo(LocalDateTime.of(2026, 10, 2, 15, 0));
        verify(bookingRepository).save(booking);
    }

    @Test
    @DisplayName("Rejects booking creation for a non-guest")
    void createBooking_nonGuest_throwsAccessDenied() {
        BookingCreateRequest request = mock(BookingCreateRequest.class);
        when(ownershipVerifier.getCurrentUser()).thenReturn(host);

        assertThatThrownBy(() -> bookingService.createBooking(request))
                .isInstanceOf(AccessDeniedException.class);

        verifyNoInteractions(propertyRepository, checkInSlotRepository, bookingRepository);
    }

    @Test
    @DisplayName("Rejects a booking that exceeds the property's guest capacity")
    void createBooking_guestCountExceedsCapacity_throws() {
        BookingCreateRequest request = mock(BookingCreateRequest.class);
        when(request.getPropertyId()).thenReturn(property.getId());
        when(request.getCheckInSlotId()).thenReturn(slot.getId());
        when(request.getCheckInDate()).thenReturn(LocalDate.of(2026, 10, 1));
        when(request.getNumberOfGuests()).thenReturn(5);
        when(ownershipVerifier.getCurrentUser()).thenReturn(guest);
        when(propertyRepository.findById(property.getId())).thenReturn(Optional.of(property));
        when(checkInSlotRepository.findById(slot.getId())).thenReturn(Optional.of(slot));

        assertThatThrownBy(() -> bookingService.createBooking(request))
                .isInstanceOf(GuestCapacityExceededException.class);

        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Rejects a booking overlapping an active booking")
    void createBooking_overlapsExistingActiveBooking_throws() {
        BookingCreateRequest request = mock(BookingCreateRequest.class);
        BookingEntity existing = BookingEntity.builder()
                .status(BookingStatus.CONFIRMED)
                .checkInDateTime(LocalDateTime.of(2026, 10, 1, 16, 0))
                .checkOutDateTime(LocalDateTime.of(2026, 10, 3, 10, 0))
                .build();
        when(request.getPropertyId()).thenReturn(property.getId());
        when(request.getCheckInSlotId()).thenReturn(slot.getId());
        when(request.getCheckInDate()).thenReturn(LocalDate.of(2026, 10, 1));
        when(request.getNumberOfGuests()).thenReturn(2);
        when(ownershipVerifier.getCurrentUser()).thenReturn(guest);
        when(propertyRepository.findById(property.getId())).thenReturn(Optional.of(property));
        when(checkInSlotRepository.findById(slot.getId())).thenReturn(Optional.of(slot));
        when(bookingRepository.findByPropertyId(property.getId())).thenReturn(List.of(existing));

        assertThatThrownBy(() -> bookingService.createBooking(request))
                .isInstanceOf(BookingAlreadyExistsException.class);

        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Updates a pending booking and ignores itself in overlap checks")
    void updateBooking_pendingBooking_savesUpdatedDates() {
        BookingUpdateRequest update = mock(BookingUpdateRequest.class);
        BookingResponse response = mock(BookingResponse.class);
        when(update.getCheckInSlotId()).thenReturn(slot.getId());
        when(update.getCheckInDate()).thenReturn(LocalDate.of(2026, 10, 5));
        when(update.getNumberOfGuests()).thenReturn(3);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(checkInSlotRepository.findById(slot.getId())).thenReturn(Optional.of(slot));
        when(bookingRepository.findByPropertyId(property.getId())).thenReturn(List.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.toBookingResponse(booking)).thenReturn(response);

        BookingResponse result = bookingService.updateBooking(booking.getId(), update);

        assertThat(result).isSameAs(response);
        assertThat(booking.getNumberOfGuests()).isEqualTo(3);
        assertThat(booking.getCheckInDateTime()).isEqualTo(LocalDateTime.of(2026, 10, 5, 15, 0));
        verify(ownershipVerifier).verifyOwnershipOrAdmin(guest);
        verify(bookingRepository).save(booking);
    }

    @Test
    @DisplayName("Approves a pending booking for its property host")
    void approveBooking_pendingBooking_confirmsBooking() {
        BookingResponse response = mock(BookingResponse.class);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.toBookingResponse(booking)).thenReturn(response);

        BookingResponse result = bookingService.approveBooking(booking.getId());

        assertThat(result).isSameAs(response);
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
        verify(ownershipVerifier).verifyOwnershipOrAdmin(host);
    }

    @Test
    @DisplayName("Rejects a pending booking and records the host's reason")
    void rejectBooking_pendingBooking_recordsReason() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.toBookingResponse(booking)).thenReturn(mock(BookingResponse.class));

        bookingService.rejectBooking(booking.getId(), "Unavailable");

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.REJECTED);
        assertThat(booking.getStatusReason()).isEqualTo("Unavailable");
        verify(ownershipVerifier).verifyOwnershipOrAdmin(host);
    }

    @Test
    @DisplayName("Cancels a pending booking and records the guest's reason")
    void cancelBooking_pendingBooking_recordsReason() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.toBookingResponse(booking)).thenReturn(mock(BookingResponse.class));

        bookingService.cancelBooking(booking.getId(), "Plans changed");

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CANCELLED);
        assertThat(booking.getStatusReason()).isEqualTo("Plans changed");
        verify(ownershipVerifier).verifyOwnershipOrAdmin(guest);
    }

    @Test
    @DisplayName("Completes a confirmed booking and records the completion reason")
    void completeBooking_confirmedBooking_marksCompleted() {
        booking.setStatus(BookingStatus.CONFIRMED);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.toBookingResponse(booking)).thenReturn(mock(BookingResponse.class));

        bookingService.completeBooking(booking.getId(), "Stay finished");

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.COMPLETED);
        assertThat(booking.getStatusReason()).isEqualTo("Stay finished");
        verify(ownershipVerifier).verifyOwnershipOrAdmin(guest);
    }

    @Test
    @DisplayName("Returns the current guest's paginated bookings")
    void getGuestBooking_guest_returnsMappedPage() {
        Pageable pageable = Pageable.ofSize(5).withPage(1);
        Page<BookingEntity> bookings = new PageImpl<>(List.of(booking), pageable, 1);
        BookingResponse response = mock(BookingResponse.class);
        when(ownershipVerifier.getCurrentUser()).thenReturn(guest);
        when(bookingRepository.findByGuestId(guest.getId(), pageable)).thenReturn(bookings);
        when(bookingMapper.toBookingResponse(booking)).thenReturn(response);

        Page<BookingResponse> result = bookingService.getGuestBooking(1, 5);

        assertThat(result.getContent()).containsExactly(response);
        verify(bookingRepository).findByGuestId(guest.getId(), pageable);
    }

    @Test
    @DisplayName("Returns the current host's paginated bookings")
    void getHostBooking_host_returnsMappedPage() {
        Pageable pageable = Pageable.ofSize(5).withPage(0);
        Page<BookingEntity> bookings = new PageImpl<>(List.of(booking), pageable, 1);
        BookingResponse response = mock(BookingResponse.class);
        when(ownershipVerifier.getCurrentUser()).thenReturn(host);
        when(bookingRepository.findByProperty_UserId(host.getId(), pageable)).thenReturn(bookings);
        when(bookingMapper.toBookingResponse(booking)).thenReturn(response);

        Page<BookingResponse> result = bookingService.getHostBooking(0, 5);

        assertThat(result.getContent()).containsExactly(response);
        verify(bookingRepository).findByProperty_UserId(host.getId(), pageable);
    }
}
