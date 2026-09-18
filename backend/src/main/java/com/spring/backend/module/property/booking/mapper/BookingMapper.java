package com.spring.backend.module.property.booking.mapper;

import com.spring.backend.module.property.booking.dto.request.BookingCreateRequest;
import com.spring.backend.module.property.booking.dto.response.BookingResponse;
import com.spring.backend.module.property.booking.entity.BookingEntity;
import com.spring.backend.module.property.checkin.entity.CheckInSlotEntity;
import com.spring.backend.module.property.property.entity.PropertyEntity;
import com.spring.backend.module.user.user.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class BookingMapper {

    public BookingEntity toBookingEntity(BookingCreateRequest request,
                                         PropertyEntity property,
                                         UserEntity guest,
                                         CheckInSlotEntity slot,
                                         LocalDate checkOutDate) {

        return BookingEntity.builder()
                .property(property)
                .guest(guest)
                .checkInSlot(slot)
                .checkInDate(request.getCheckInDate())
                .checkOutDate(checkOutDate)
                .checkInTime(slot.getStartTime())
                .checkOutTime(slot.getEndTime())
                .numberOfGuests(request.getNumberOfGuests())
                .build();
    }

    public BookingResponse toBookingResponse(BookingEntity entity) {

        return BookingResponse.builder()
                .id(entity.getId())
                .propertyId(entity.getProperty().getId())
                .propertyTitle(entity.getProperty().getTitle())
                .guestId(entity.getGuest().getId())
                .guestName(entity.getGuest().getName())
                .checkInSlotId(entity.getCheckInSlot().getId())
                .checkInDate(entity.getCheckInDate())
                .checkOutDate(entity.getCheckOutDate())
                .checkInTime(entity.getCheckInTime())
                .checkOutTime(entity.getCheckOutTime())
                .totalPrice(entity.getTotalPrice())
                .status(entity.getStatus())
                .build();
    }
}