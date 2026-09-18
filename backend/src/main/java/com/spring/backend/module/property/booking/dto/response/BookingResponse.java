package com.spring.backend.module.property.booking.dto.response;

import com.spring.backend.module.property.booking.enums.BookingStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
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
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private LocalTime checkInTime;
    private LocalTime checkOutTime;
    private Integer numberOfGuests;
    private Double totalPrice;
    private BookingStatus status;
}