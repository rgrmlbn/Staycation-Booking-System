package com.spring.backend.module.property.review.service.impl;

import com.spring.backend.module.property.review.dto.request.ReviewCreateRequest;
import com.spring.backend.module.property.review.dto.request.ReviewUpdateRequest;
import com.spring.backend.module.property.review.dto.response.ReviewResponse;
import com.spring.backend.module.property.review.service.interfaces.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    @Override
    public Page<ReviewResponse> getAllReview(int page, int size) {
        return null;
    }

    @Override
    public Page<ReviewResponse> getAllReviewByProperty(Long propertyId, int page, int size) {
        return null;
    }

    @Override
    public Page<ReviewResponse> getReviewByBooking(Long bookingId, int page, int size) {
        return null;
    }

    @Override
    public Page<ReviewResponse> getAllReviewByGuest(Long guestId, int page, int size) {
        return null;
    }

    @Override
    public ReviewResponse createReview(ReviewCreateRequest request) {
        return null;
    }

    @Override
    public ReviewResponse updateReview(Long reviewId, ReviewUpdateRequest update) {
        return null;
    }

    @Override
    public void deleteReviewById(Long reviewId) {

    }
}

