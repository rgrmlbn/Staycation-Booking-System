package com.spring.backend.module.property.booking.dto.response;

import com.spring.backend.module.property.booking.enums.BookingStatus;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Builder
public class BookingResponse {

    private Long id;
    private Long propertyId;
    private String propertyTitle;
    private Long guestId;
    private String guestName;
    private Long checkInSlotId;
    private LocalDateTime checkInDateTime;
    private LocalDateTime checkOutDateTime;
    private Integer numberOfGuests;
    private Double totalPrice;
    private BookingStatus status;
}