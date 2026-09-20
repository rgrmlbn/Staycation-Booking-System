package com.spring.backend.module.property.booking.service.interfaces;

import com.spring.backend.module.property.booking.dto.request.BookingCreateRequest;
import com.spring.backend.module.property.booking.dto.request.BookingUpdateRequest;
import com.spring.backend.module.property.booking.dto.response.BookingResponse;
import org.springframework.data.domain.Page;

public interface BookingService {

    BookingResponse createBooking(BookingCreateRequest request);

    // guestId/hostId dropped: ownership is verified against the authenticated
    // user inside the implementation (via OwnershipVerifier), so there was
    // never anything meaningful for a caller to pass here.
    BookingResponse updateBooking(Long bookingId, BookingUpdateRequest update);

    BookingResponse approveBooking(Long bookingId);

    BookingResponse rejectBooking(Long bookingId, String reason);

    BookingResponse cancelBooking(Long bookingId, String reason);

    BookingResponse completeBooking(Long bookingId, String reason, Integer rate);

    // page/size follow the same pagination pattern as
    // PropertyService#getAllMyProperties
    Page<BookingResponse> getGuestBooking(int page, int size);

    Page<BookingResponse> getHostBooking(int page, int size);
}