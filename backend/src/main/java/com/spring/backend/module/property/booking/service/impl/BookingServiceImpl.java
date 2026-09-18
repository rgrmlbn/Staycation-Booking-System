package com.spring.backend.module.property.booking.service.impl;

import com.spring.backend.exception.common.ResourceNotFoundException;
import com.spring.backend.exception.property.property.GuestCapacityExceededException;
import com.spring.backend.exception.property.property.OverlappingTimeSlotException;
import com.spring.backend.module.property.booking.dto.request.BookingCreateRequest;
import com.spring.backend.module.property.booking.dto.request.BookingUpdateRequest;
import com.spring.backend.module.property.booking.dto.response.BookingResponse;
import com.spring.backend.module.property.booking.entity.BookingEntity;
import com.spring.backend.module.property.booking.mapper.BookingMapper;
import com.spring.backend.module.property.booking.repository.BookingRepository;
import com.spring.backend.module.property.booking.service.interfaces.BookingService;
import com.spring.backend.module.property.property.entity.CheckInSlotEntity;
import com.spring.backend.module.property.property.entity.PropertyEntity;
import com.spring.backend.module.property.property.repository.PropertyRepository;
import com.spring.backend.module.shared.util.OwnershipVerifier;
import com.spring.backend.module.user.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingMapper bookingMapper;
    private final BookingRepository bookingRepository;
    private final OwnershipVerifier ownershipVerifier;
    private final PropertyRepository propertyRepository;

    // SUB-HELPER: Validates the requested guest count doesn't exceed the property's capacity
    private void validateGuestCount(Integer requestedGuests, Integer maxGuests) {
        if (requestedGuests > maxGuests) {
            throw new GuestCapacityExceededException();
        }
    }

    // SUB-HELPER: Finds the requested slot among the property's slots
    private CheckInSlotEntity resolveSlot(PropertyEntity property, Long checkInSlotId) {
        return property.getCheckInSlots().stream()
                .filter(slot -> slot.getId().equals(checkInSlotId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Check-in slot"));
    }

    // SUB-HELPER: A slot that ends at or before its own start time is treated as crossing midnight,
    // so checkout lands on the next calendar day.
    private LocalDate resolveCheckOutDate(LocalDate checkInDate, LocalTime checkInTime, LocalTime checkOutTime) {
        return checkOutTime.isAfter(checkInTime) ? checkInDate : checkInDate.plusDays(1);
    }

    // SUB-HELPER: Validates the requested slot doesn't overlap with an existing active booking on the property.
    private void validateNoOverlappingBooking(Long propertyId, LocalDate checkInDate, LocalTime checkInTime,
                                              LocalDate checkOutDate, LocalTime checkOutTime) {

        boolean hasOverlap = bookingRepository.existsOverlappingBooking(
                propertyId, checkInDate, checkInTime, checkOutDate, checkOutTime);

        if (hasOverlap) {
            throw new OverlappingTimeSlotException();
        }
    }

    @Override
    @Transactional
    public BookingResponse createBooking(BookingCreateRequest request) {

        UserEntity guest = ownershipVerifier.getCurrentUser();

        PropertyEntity property = propertyRepository.findById(request.getPropertyId())
                .orElseThrow(() -> new ResourceNotFoundException("Property"));

        CheckInSlotEntity slot = resolveSlot(property, request.getCheckInSlotId());

        validateGuestCount(request.getNumberOfGuests(), property.getMaxGuests());

        LocalDate checkInDate = request.getCheckInDate();
        LocalTime checkInTime = slot.getStartTime();
        LocalTime checkOutTime = slot.getEndTime();
        LocalDate checkOutDate = resolveCheckOutDate(checkInDate, checkInTime, checkOutTime);

        validateNoOverlappingBooking(property.getId(), checkInDate, checkInTime, checkOutDate, checkOutTime);

        BookingEntity booking = bookingMapper.toBookingEntity(
                request, property, guest, slot, checkOutDate);

        booking.setTotalPrice(slot.getPrice());

        BookingEntity savedBooking = bookingRepository.save(booking);

        return bookingMapper.toBookingResponse(savedBooking);
    }

    @Override
    public BookingResponse updateBooking(Long guestId, Long bookingId, BookingUpdateRequest update) {
        return null;
    }

    @Override
    public BookingResponse approveBooking(Long hostId, Long bookingId) {
        return null;
    }

    @Override
    public BookingResponse rejectBooking(Long id, Long bookingId, String reason) {
        return null;
    }

    @Override
    public BookingResponse cancelBooking(Long guestId, Long bookingId) {
        return null;
    }

    @Override
    public BookingResponse completeBooking(Long guestId, Long bookingId) {
        return null;
    }

    @Override
    public BookingResponse getGuestBooking(Long guestId) {
        return null;
    }

    @Override
    public BookingResponse getHostBooking(Long hostId) {
        return null;
    }
}