package com.spring.backend.module.property.booking.service.impl;

import com.spring.backend.exception.common.ResourceNotFoundException;
import com.spring.backend.exception.property.booking.BookingAlreadyExistsException;
import com.spring.backend.exception.property.property.GuestCapacityExceededException;
import com.spring.backend.exception.property.property.OverlappingTimeSlotException;
import com.spring.backend.module.property.booking.dto.request.BookingCreateRequest;
import com.spring.backend.module.property.booking.dto.request.BookingUpdateRequest;
import com.spring.backend.module.property.booking.dto.response.BookingResponse;
import com.spring.backend.module.property.booking.entity.BookingEntity;
import com.spring.backend.module.property.booking.enums.BookingStatus;
import com.spring.backend.module.property.booking.mapper.BookingMapper;
import com.spring.backend.module.property.booking.repository.BookingRepository;
import com.spring.backend.module.property.booking.service.interfaces.BookingService;
import com.spring.backend.module.property.checkin.entity.CheckInSlotEntity;
import com.spring.backend.module.property.checkin.repository.CheckInSlotRepository;
import com.spring.backend.module.property.property.entity.PropertyEntity;
import com.spring.backend.module.property.property.enums.PropertyStatus;
import com.spring.backend.module.property.property.repository.PropertyRepository;
import com.spring.backend.module.shared.util.OwnershipVerifier;
import com.spring.backend.module.user.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingMapper bookingMapper;
    private final BookingRepository bookingRepository;
    private final OwnershipVerifier ownershipVerifier;
    private final PropertyRepository propertyRepository;
    private final CheckInSlotRepository checkInSlotRepository;

    @Override
    @Transactional
    public BookingResponse createBooking(BookingCreateRequest request) {

        // Get the currently logged-in user as the guest
        UserEntity guest = ownershipVerifier.getCurrentUser();

        // Find the property being booked
        PropertyEntity property = propertyRepository.findById(request.getPropertyId())
                .orElseThrow(() -> new ResourceNotFoundException("Property"));

        // Find the check-in option selected by the guest
        CheckInSlotEntity slot = checkInSlotRepository.findById(request.getCheckInSlotId())
                .orElseThrow(() -> new ResourceNotFoundException("Check-in slot"));

        // Make sure the selected check-in option belongs to the selected property
        if (!slot.getProperty().getId().equals(property.getId())) {
            throw new ResourceNotFoundException("Check-in slot");
        }

        // Combine the check-in date and selected start time
        LocalDateTime checkInDateTime =
                LocalDateTime.of(request.getCheckInDate(), slot.getStartTime());

        // Calculate checkout based on the selected duration option
        LocalDateTime checkOutDateTime =
                checkInDateTime.plusHours(slot.getDurationHours());

        // Check if the property already has a pending or confirmed booking
        // during this time. Pulled as a plain list and checked with if
        // statements here, rather than filtering status/time range inside the
        // repository query.
        List<BookingEntity> existingBookings = bookingRepository.findByPropertyId(property.getId());

        boolean hasOverlappingBooking = false;

        for (BookingEntity existingBooking : existingBookings) {

            boolean isActive = existingBooking.getStatus() == BookingStatus.PENDING
                    || existingBooking.getStatus() == BookingStatus.CONFIRMED;

            boolean overlapsInTime = existingBooking.getCheckInDateTime().isBefore(checkOutDateTime)
                    && existingBooking.getCheckOutDateTime().isAfter(checkInDateTime);

            if (isActive && overlapsInTime) {
                hasOverlappingBooking = true;
                break;
            }
        }

        if (hasOverlappingBooking) {
            throw new BookingAlreadyExistsException();
        }

        // Create the booking
        BookingEntity booking = bookingMapper.toBookingEntity(
                request,
                property,
                guest,
                slot,
                checkInDateTime,
                checkOutDateTime
        );

        property.setStatus(PropertyStatus.BOOKED);

        BookingEntity savedBooking = bookingRepository.save(booking);

        return bookingMapper.toBookingResponse(savedBooking);
    }

    @Override
    @Transactional
    public BookingResponse updateBooking(Long bookingId, BookingUpdateRequest update) {

        // Fetch by ID alone, then let OwnershipVerifier confirm the caller is
        // either this booking's guest or an admin — the same pattern
        // PropertyServiceImpl uses for updateProperty/deleteProperty. This is
        // safer than filtering the query by a guestId passed into the method,
        // since that value could be spoofed by the caller; ownershipVerifier
        // checks against the actual authenticated user instead.
        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking"));

        ownershipVerifier.verifyOwnershipOrAdmin(booking.getGuest());

        // Only a booking still awaiting host action can be changed
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException("Only pending bookings can be updated");
        }

        PropertyEntity property = booking.getProperty();

        CheckInSlotEntity slot = checkInSlotRepository.findById(update.getCheckInSlotId())
                .orElseThrow(() -> new ResourceNotFoundException("Check-in slot"));

        if (!slot.getProperty().getId().equals(property.getId())) {
            throw new ResourceNotFoundException("Check-in slot");
        }

        LocalDateTime checkInDateTime =
                LocalDateTime.of(update.getCheckInDate(), slot.getStartTime());

        LocalDateTime checkOutDateTime =
                checkInDateTime.plusHours(slot.getDurationHours());

        // Same overlap check as createBooking, but skip this booking's own
        // current row so it doesn't collide with itself.
        List<BookingEntity> existingBookings = bookingRepository.findByPropertyId(property.getId());

        boolean hasOverlappingBooking = false;

        for (BookingEntity existingBooking : existingBookings) {

            if (existingBooking.getId().equals(booking.getId())) {
                continue;
            }

            boolean isActive = existingBooking.getStatus() == BookingStatus.PENDING
                    || existingBooking.getStatus() == BookingStatus.CONFIRMED;

            boolean overlapsInTime = existingBooking.getCheckInDateTime().isBefore(checkOutDateTime)
                    && existingBooking.getCheckOutDateTime().isAfter(checkInDateTime);

            if (isActive && overlapsInTime) {
                hasOverlappingBooking = true;
                break;
            }
        }

        if (hasOverlappingBooking) {
            throw new BookingAlreadyExistsException();
        }

        booking.setCheckInSlot(slot);
        booking.setCheckInDateTime(checkInDateTime);
        booking.setCheckOutDateTime(checkOutDateTime);

        BookingEntity updated = bookingRepository.save(booking);

        return bookingMapper.toBookingResponse(updated);
    }

    @Override
    @Transactional
    public BookingResponse approveBooking(Long bookingId) {

        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking"));

        ownershipVerifier.verifyOwnershipOrAdmin(booking.getProperty().getUser());

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException("Only pending bookings can be approved");
        }

        booking.setStatus(BookingStatus.CONFIRMED);

        BookingEntity updated = bookingRepository.save(booking);

        return bookingMapper.toBookingResponse(updated);
    }

    @Override
    @Transactional
    public BookingResponse rejectBooking(Long bookingId, String reason) {

        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking"));

        ownershipVerifier.verifyOwnershipOrAdmin(booking.getProperty().getUser());

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException("Only pending bookings can be rejected");
        }

        booking.setStatus(BookingStatus.REJECTED);

        // Free up the property again now that the booking won't go ahead
        booking.getProperty().setStatus(PropertyStatus.AVAILABLE);

        BookingEntity updated = bookingRepository.save(booking);

        return bookingMapper.toBookingResponse(updated);
    }

    @Override
    @Transactional
    public BookingResponse cancelBooking(Long bookingId) {

        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking"));

        ownershipVerifier.verifyOwnershipOrAdmin(booking.getGuest());

        if (booking.getStatus() != BookingStatus.PENDING
                && booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Only pending or confirmed bookings can be cancelled");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking.getProperty().setStatus(PropertyStatus.AVAILABLE);

        BookingEntity updated = bookingRepository.save(booking);

        return bookingMapper.toBookingResponse(updated);
    }

    @Override
    @Transactional
    public BookingResponse completeBooking(Long bookingId) {

        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking"));

        ownershipVerifier.verifyOwnershipOrAdmin(booking.getGuest());

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Only confirmed bookings can be completed");
        }

        if (booking.getCheckOutDateTime().isAfter(LocalDateTime.now())) {
            throw new IllegalStateException("Booking cannot be completed before its checkout time");
        }

        booking.setStatus(BookingStatus.COMPLETED);
        booking.getProperty().setStatus(PropertyStatus.AVAILABLE);

        BookingEntity updated = bookingRepository.save(booking);

        return bookingMapper.toBookingResponse(updated);
    }

    // Get all bookings made by the current user as a guest, with pagination support
    @Override
    public Page<BookingResponse> getGuestBooking(int page, int size) {

        UserEntity guest = ownershipVerifier.getCurrentUser();

        Pageable pageable = Pageable.ofSize(size).withPage(page);

        Page<BookingEntity> bookings = bookingRepository.findByGuestId(guest.getId(), pageable);

        return bookings.map(bookingEntity -> bookingMapper.toBookingResponse(bookingEntity));
    }

    // Get all bookings across properties owned by the current user as a host, with pagination support
    @Override
    public Page<BookingResponse> getHostBooking(int page, int size) {

        UserEntity host = ownershipVerifier.getCurrentUser();

        Pageable pageable = Pageable.ofSize(size).withPage(page);

        Page<BookingEntity> bookings = bookingRepository.findByProperty_UserId(host.getId(), pageable);

        return bookings.map(bookingEntity -> bookingMapper.toBookingResponse(bookingEntity));
    }
}