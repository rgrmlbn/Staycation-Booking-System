package com.spring.backend.module.property.booking.service.interfaces;

import com.spring.backend.module.property.booking.dto.request.BookingCreateRequest;
import com.spring.backend.module.property.booking.dto.request.BookingUpdateRequest;
import com.spring.backend.module.property.booking.dto.response.BookingResponse;

public interface BookingService {

    BookingResponse createBooking(BookingCreateRequest request);
    BookingResponse updateBooking(Long guestId, Long bookingId, BookingUpdateRequest update);
    BookingResponse approveBooking(Long hostId, Long bookingId);
    BookingResponse rejectBooking(Long id, Long bookingId, String reason);
    BookingResponse cancelBooking(Long guestId, Long bookingId);
    BookingResponse completeBooking(Long guestId, Long bookingId);
    BookingResponse getGuestBooking(Long guestId);
    BookingResponse getHostBooking(Long hostId);

}
