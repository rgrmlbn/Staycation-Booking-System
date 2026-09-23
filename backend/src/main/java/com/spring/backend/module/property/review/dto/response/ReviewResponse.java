package com.spring.backend.module.property.review.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewResponse {

    private Long id;
    private Long bookingId;
    private Long propertyId;
    private Long guestId;
    private Integer rating;
    private String comment;
}