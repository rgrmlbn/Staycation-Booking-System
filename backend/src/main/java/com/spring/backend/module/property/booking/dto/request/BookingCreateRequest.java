package com.spring.backend.module.property.booking.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class BookingCreateRequest {

    @NotNull(message = "Property ID is required")
    private Long propertyId;

    @NotNull(message = "Check-In Slot is required")
    private Long checkInSlotId;

    @NotNull(message = "Check-In Date is required")
    @Future(message = "Invalid Date, Must be in the future")
    private LocalDate checkInDate;

    @NotNull(message = "Number of Guest is required")
    @Min(value = 1, message = "Number guests should be 1 or more")
    private Integer numberOfGuests;
}