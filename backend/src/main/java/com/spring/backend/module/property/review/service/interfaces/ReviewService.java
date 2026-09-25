package com.spring.backend.module.property.review.service.interfaces;

import com.spring.backend.module.property.review.dto.request.ReviewCreateRequest;
import com.spring.backend.module.property.review.dto.request.ReviewUpdateRequest;
import com.spring.backend.module.property.review.dto.response.ReviewResponse;
import org.springframework.data.domain.Page;

public interface ReviewService {

    Page<ReviewResponse> getAllReview(int page, int size);
    Page<ReviewResponse> getAllReviewByProperty(Long propertyId, int page, int size);
    ReviewResponse getReviewByBooking(Long bookingId);
    Page<ReviewResponse> getAllReviewByGuest(Long guestId, int page, int size);
    ReviewResponse createReview(ReviewCreateRequest request);
    ReviewResponse updateReview(Long reviewId, ReviewUpdateRequest update);
    void deleteReviewById(Long reviewId);


}