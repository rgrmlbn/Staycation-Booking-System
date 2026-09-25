package com.spring.backend.module.property.review.service.impl;

import com.spring.backend.exception.common.ResourceNotFoundException;
import com.spring.backend.exception.property.rating.ReviewAlreadyExistsException;
import com.spring.backend.module.property.booking.entity.BookingEntity;
import com.spring.backend.module.property.booking.enums.BookingStatus;
import com.spring.backend.module.property.booking.repository.BookingRepository;
import com.spring.backend.module.property.property.entity.PropertyEntity;
import com.spring.backend.module.property.property.repository.PropertyRepository;
import com.spring.backend.module.property.review.dto.request.ReviewCreateRequest;
import com.spring.backend.module.property.review.dto.request.ReviewUpdateRequest;
import com.spring.backend.module.property.review.dto.response.ReviewResponse;
import com.spring.backend.module.property.review.entity.ReviewEntity;
import com.spring.backend.module.property.review.mapper.ReviewMapper;
import com.spring.backend.module.property.review.repository.ReviewRepository;
import com.spring.backend.module.property.review.service.interfaces.ReviewService;
import com.spring.backend.module.shared.util.OwnershipVerifier;
import com.spring.backend.module.user.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;
    private final PropertyRepository propertyRepository;
    private final ReviewMapper reviewMapper;
    private final OwnershipVerifier ownershipVerifier;

    @Override
    public Page<ReviewResponse> getAllReview(int page, int size) {

        Pageable pageable = Pageable.ofSize(size).withPage(page);

        Page<ReviewEntity> reviews = reviewRepository.findAll(pageable);

        return reviews.map(reviewEntity -> reviewMapper.toResponse(reviewEntity));
    }

    @Override
    public Page<ReviewResponse> getAllReviewByProperty(Long propertyId, int page, int size) {

        // Fail fast if the property doesn't exist, same pattern as PropertyServiceImpl.getPropertyById
        propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property"));

        Pageable pageable = Pageable.ofSize(size).withPage(page);

        Page<ReviewEntity> reviews = reviewRepository.findByPropertyId(propertyId, pageable);

        return reviews.map(reviewEntity -> reviewMapper.toResponse(reviewEntity));
    }

    @Override
    public ReviewResponse getReviewByBooking(Long bookingId) {

        ReviewEntity reviews = reviewRepository.findByBookingId(bookingId);

        return reviewMapper.toResponse(reviews);
    }

    @Override
    public Page<ReviewResponse> getAllReviewByGuest(Long guestId, int page, int size) {

        Pageable pageable = Pageable.ofSize(size).withPage(page);

        Page<ReviewEntity> reviews = reviewRepository.findByGuestId(guestId, pageable);

        return reviews.map(reviewEntity -> reviewMapper.toResponse(reviewEntity));
    }

    // Single source of truth for keeping PropertyEntity.reviewScore / reviewCount
    // in sync with the reviews table. Called inside the same transaction as
    // every write (create/update/delete), so the cached columns never drift.
    private void recalculatePropertyRating(PropertyEntity property) {

        List<ReviewEntity> reviews = reviewRepository.findAllByPropertyId(property.getId());

        double avgRating = 0.0;

        if (!reviews.isEmpty()) {
            int totalRating = 0;

            for (ReviewEntity review : reviews) {
                totalRating += review.getRating();
            }

            avgRating = (double) totalRating / reviews.size();
        }

        int reviewCount = reviews.size();

        property.setReviewScore(Math.round(avgRating * 10) / 10.0);
        property.setReviewCount(reviewCount);

        propertyRepository.save(property);
    }

    @Override
    @Transactional
    public ReviewResponse createReview(ReviewCreateRequest request) {

        UserEntity guest = ownershipVerifier.getCurrentUser();

        BookingEntity booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking"));

        // Only the guest who made the booking can leave a review for it
        ownershipVerifier.verifyOwnershipOrAdmin(booking.getGuest());

        if (booking.getStatus() != BookingStatus.COMPLETED) {
            throw new IllegalStateException("Only completed bookings can be reviewed");
        }

        if (reviewRepository.existsByBookingId(booking.getId())) {
            throw new ReviewAlreadyExistsException();
        }

        PropertyEntity property = booking.getProperty();

        ReviewEntity review = reviewMapper.toEntity(request, booking, property, guest);

        ReviewEntity saved = reviewRepository.save(review);

        recalculatePropertyRating(property);

        return reviewMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public ReviewResponse updateReview(Long reviewId, ReviewUpdateRequest update) {

        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review"));

        ownershipVerifier.verifyOwnershipOrAdmin(review.getGuest());

        if (update.getRating() != null) {
            review.setRating(update.getRating());
        }
        if (update.getComment() != null) {
            review.setComment(update.getComment());
        }

        ReviewEntity updated = reviewRepository.save(review);

        recalculatePropertyRating(review.getProperty());

        return reviewMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteReviewById(Long reviewId) {

        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review"));

        ownershipVerifier.verifyOwnershipOrAdmin(review.getGuest());

        PropertyEntity property = review.getProperty();

        reviewRepository.delete(review);

        recalculatePropertyRating(property);
    }

}