package com.spring.backend.module.property.booking.service.impl;

import com.spring.backend.module.property.booking.mapper.BookingMapper;
import com.spring.backend.module.property.booking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl {

    private final BookingMapper bookingMapper;
    private final BookingRepository bookingRepository;
}
