package com.spring.backend.module.property.review.controller;

import com.spring.backend.module.property.review.dto.request.ReviewCreateRequest;
import com.spring.backend.module.property.review.dto.request.ReviewUpdateRequest;
import com.spring.backend.module.property.review.dto.response.ReviewResponse;
import com.spring.backend.module.property.review.service.interfaces.ReviewService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Validated
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<Page<ReviewResponse>> getAllReview(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(reviewService.getAllReview(page, size));
    }

    @GetMapping("/property/{id}")
    public ResponseEntity<Page<ReviewResponse>> getAllReviewByProperty(
            @Positive @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(reviewService.getAllReviewByProperty(id, page, size));
    }

    @GetMapping("/booking/{id}")
    public ResponseEntity<ReviewResponse> getReviewByBooking(
            @Positive @PathVariable Long id) {

        return ResponseEntity.ok(reviewService.getReviewByBooking(id));
    }

    @GetMapping("/guest/{id}")
    public ResponseEntity<Page<ReviewResponse>> getAllReviewByGuest(
            @Positive @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(reviewService.getAllReviewByGuest(id, page, size));
    }

    @PostMapping("/create")
    public ResponseEntity<ReviewResponse> createReview(
            @RequestBody @Valid ReviewCreateRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.createReview(request));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ReviewResponse> updateReview(
            @Positive @PathVariable Long id,
            @RequestBody @Valid ReviewUpdateRequest request) {

        return ResponseEntity.ok(reviewService.updateReview(id, request));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteReview(
            @Positive @PathVariable Long id) {

        reviewService.deleteReviewById(id);

        return ResponseEntity.noContent().build();
    }
}