package com.spring.backend.module.property.booking.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class BookingUpdateRequest {

    private Long checkInSlotId;

    @Future(message = "Invalid Date, Must be in the future")
    private LocalDate checkInDate;

    @Min(value = 1, message = "Number guests should be 1 or more")
    private Integer numberOfGuests;
}
