package com.spring.backend.module.property.booking.dto.request;

import com.spring.backend.module.property.property.entity.PropertyEntity;
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
public class BookingCreate {

    @NotNull(message = "Property ID is required")
    private PropertyEntity propertyId;

    @NotNull(message = "Check-In Date is required")
    @Future(message = "Invalid Date, Must be in the future")
    private LocalDate checkInDate;

    @NotNull(message = "Check-Out Date is required")
    @Future(message = "Invalid Date, Must be in the future")
    private LocalDate checkOutDate;

    @NotNull(message = "Check-In Time is required")
    private LocalTime checkInTime;

    @NotNull(message = "Check-Out Time is required")
    private LocalTime checkOutTime;

    @NotNull(message = "Number of Guest is required")
    @Min(value = 1, message = "Number guests should be 1 or more")
    private Integer numberOfGuests;

}
