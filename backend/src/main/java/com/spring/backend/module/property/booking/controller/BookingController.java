package com.spring.backend.module.property.booking.controller;

import com.spring.backend.module.property.booking.dto.request.BookingCreateRequest;
import com.spring.backend.module.property.booking.dto.response.BookingResponse;
import com.spring.backend.module.property.booking.service.interfaces.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/create")
    ResponseEntity<BookingResponse> createBooking(@RequestBody @Valid BookingCreateRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.createBooking(request));
    }
}
