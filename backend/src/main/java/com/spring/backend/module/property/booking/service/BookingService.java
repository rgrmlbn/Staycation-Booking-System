package com.spring.backend.module.property.booking.service;

import com.spring.backend.module.property.booking.dto.request.BookingCreate;
import com.spring.backend.module.property.booking.dto.response.BookingResponse;
import com.spring.backend.module.property.booking.entity.BookingEntity;

public interface BookingService {

    BookingResponse createBooking(Long guestId, BookingCreate request);
    BookingResponse approveBooking(Long hostId, Long bookingId);

}
