package com.spring.backend.module.property.booking.dto.response;

import com.spring.backend.module.property.booking.enums.BookingStatus;
import com.spring.backend.module.property.property.entity.PropertyEntity;
import com.spring.backend.module.user.user.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
public class BookingResponse {

    private Long id;
    private PropertyEntity property;
    private UserEntity guest;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private LocalTime checkInTime;
    private LocalTime checkOutTime;
    private Integer numberOfGuests;
    private Double totalPrice;
    private BookingStatus status;
}
