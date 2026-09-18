package com.spring.backend.module.property.booking.service.impl;

import com.spring.backend.module.property.booking.dto.request.BookingCreateRequest;
import com.spring.backend.module.property.booking.dto.request.BookingUpdateRequest;
import com.spring.backend.module.property.booking.dto.response.BookingResponse;
import com.spring.backend.module.property.booking.entity.BookingEntity;
import com.spring.backend.module.property.booking.mapper.BookingMapper;
import com.spring.backend.module.property.booking.repository.BookingRepository;
import com.spring.backend.module.property.booking.service.interfaces.BookingService;
import com.spring.backend.module.shared.util.OwnershipVerifier;
import com.spring.backend.module.user.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.swing.text.html.parser.Entity;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingMapper bookingMapper;
    private final BookingRepository bookingRepository;
    private final OwnershipVerifier ownershipVerifier;


    @Override
    public BookingResponse createBooking(Long guestId, BookingCreateRequest request) {

        UserEntity user = ownershipVerifier.getCurrentUser();

        return null;
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
