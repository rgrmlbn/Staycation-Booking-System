package com.spring.backend.module.property.review.controller;

import com.spring.backend.module.property.review.dto.request.ReviewCreateRequest;
import com.spring.backend.module.property.review.dto.request.ReviewUpdateRequest;
import com.spring.backend.module.property.review.dto.response.ReviewResponse;
import com.spring.backend.module.property.review.entity.ReviewEntity;
import com.spring.backend.module.property.review.service.interfaces.ReviewService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    ResponseEntity<Page<ReviewResponse>> getAllReview(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(reviewService.getAllReview(page, size));
    }

    @GetMapping("/property")
    ResponseEntity<Page<ReviewResponse>> getAllReviewByProperty(
            @Positive @PathVariable Long propertyId,
            int page,
            int size) {

        return ResponseEntity.ok(reviewService.getAllReviewByProperty(propertyId, page, size));
    }

    @GetMapping("/booking")
    ResponseEntity<ReviewResponse> getReviewByBooking(
            @Positive @PathVariable Long bookingId) {

        return ResponseEntity.ok(reviewService.getReviewByBooking(bookingId));
    }

    @GetMapping("/guest")
    ResponseEntity<Page<ReviewResponse>> getAllReviewByGuest(
            @Positive @PathVariable Long guestId,
            int page,
            int size) {

        return ResponseEntity.ok(reviewService.getAllReviewByGuest(guestId, page, size));
    }

    @PostMapping("/create")
    ResponseEntity<ReviewResponse> createReview(@RequestBody @Valid ReviewCreateRequest request){

        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.createReview(request));
    }

    @PostMapping("/update")
    ResponseEntity<ReviewResponse> updateReview(@Positive @PathVariable Long reviewId, @RequestBody @Valid ReviewUpdateRequest request){

        return ResponseEntity.ok(reviewService.updateReview(reviewId, request));
    }

    @DeleteMapping("/delete")
    ResponseEntity<Void> deleteReview(@Positive @PathVariable Long reviewId){

        reviewService.deleteReviewById(reviewId);

        return ResponseEntity.noContent().build();
    }
}
