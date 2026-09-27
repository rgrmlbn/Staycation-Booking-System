package com.spring.backend.module.property.booking.controller;

import com.spring.backend.module.property.booking.dto.request.BookingCreateRequest;
import com.spring.backend.module.property.booking.dto.request.BookingUpdateRequest;
import com.spring.backend.module.property.booking.dto.response.BookingResponse;
import com.spring.backend.module.property.booking.service.interfaces.BookingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    // Guest-initiated: change a pending booking's check-in slot/date
    @PatchMapping("/{id}")
    ResponseEntity<BookingResponse> updateBooking(
            @Positive @PathVariable Long id,
            @RequestBody @Valid BookingUpdateRequest request) {

        return ResponseEntity.ok(bookingService.updateBooking(id, request));
    }

    // Host-initiated: accept a pending booking
    @PatchMapping("/{id}/approve")
    ResponseEntity<BookingResponse> approveBooking(@Positive @PathVariable Long id) {

        return ResponseEntity.ok(bookingService.approveBooking(id));
    }

    // Host-initiated: decline a pending booking, with an optional reason
    @PatchMapping("/{id}/reject")
    ResponseEntity<BookingResponse> rejectBooking(
            @Positive @PathVariable Long id,
            @RequestParam(required = true)
            @Size(min = 10, max = 500) String reason) {

        return ResponseEntity.ok(
                bookingService.rejectBooking(id, reason)
        );
    }

    // Guest-initiated: cancel a pending or confirmed booking
    @PatchMapping("/{id}/cancel")
    ResponseEntity<BookingResponse> cancelBooking(
            @Positive @PathVariable Long id,
            @RequestParam(required = true)
            @Size(min = 10, max = 500) String reason) {

        return ResponseEntity.ok(bookingService.cancelBooking(id, reason));
    }

    // Guest-initiated: mark a confirmed, past-checkout booking as complete
    @PatchMapping("/{id}/complete")
    ResponseEntity<BookingResponse> completeBooking(
            @Positive @PathVariable Long id,
            @RequestParam(required = false) String reason) {

        return ResponseEntity.ok(bookingService.completeBooking(id, reason));
    }

    // The current user's bookings as a guest, with pagination support
    @GetMapping("/guest")
    ResponseEntity<Page<BookingResponse>> getGuestBooking(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(bookingService.getGuestBooking(page, size));
    }

    // The current user's bookings as a host, with pagination support
    @GetMapping("/host")
    ResponseEntity<Page<BookingResponse>> getHostBooking(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(bookingService.getHostBooking(page, size));
    }
}