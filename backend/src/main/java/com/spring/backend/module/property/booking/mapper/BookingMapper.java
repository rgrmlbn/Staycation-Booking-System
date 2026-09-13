package com.spring.backend.module.property.booking.mapper;

import com.spring.backend.module.property.booking.dto.request.BookingCreate;
import com.spring.backend.module.property.booking.dto.response.BookingResponse;
import com.spring.backend.module.property.booking.entity.BookingEntity;
import com.spring.backend.module.user.user.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {

    public BookingEntity toBookingEntity(BookingCreate request, UserEntity user){

        return BookingEntity.builder()
                .property(request.getPropertyId())
                .guest(user)
                .checkInDate(request.getCheckInDate())
                .checkOutDate(request.getCheckOutDate())
                .checkInTime(request.getCheckInTime())
                .checkOutTime(request.getCheckOutTime())
                .numberOfGuests(request.getNumberOfGuests())
                .build();
    }

    public BookingResponse toBookingResponse(BookingEntity entity){

        return BookingResponse.builder()
                .id(entity.getId())
                .property(entity.getProperty())
                .guest(entity.getGuest())
                .checkInDate(entity.getCheckInDate())
                .checkOutDate(entity.getCheckOutDate())
                .checkInTime(entity.getCheckInTime())
                .checkOutTime(entity.getCheckOutTime())
                .totalPrice(entity.getTotalPrice())
                .status(entity.getStatus())
                .build();
    }
}
