package com.spring.backend.module.property.review.mapper;

import com.spring.backend.module.property.booking.entity.BookingEntity;
import com.spring.backend.module.property.property.entity.PropertyEntity;
import com.spring.backend.module.property.review.dto.request.ReviewCreateRequest;
import com.spring.backend.module.property.review.dto.response.ReviewResponse;
import com.spring.backend.module.property.review.entity.ReviewEntity;
import com.spring.backend.module.user.user.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapper {

    public ReviewEntity toEntity(ReviewCreateRequest request,
                                 BookingEntity booking,
                                 PropertyEntity property,
                                 UserEntity guest) {

        return ReviewEntity.builder()
                .booking(booking)
                .property(property)
                .guest(guest)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();
    }

    public ReviewResponse toResponse(ReviewEntity entity){

        return ReviewResponse.builder()
                .id(entity.getId())
                .bookingId(entity.getBooking().getId())
                .propertyId(entity.getProperty().getId())
                .guestId(entity.getGuest().getId())
                .rating(entity.getRating())
                .comment(entity.getComment())
                .build();
    }
}