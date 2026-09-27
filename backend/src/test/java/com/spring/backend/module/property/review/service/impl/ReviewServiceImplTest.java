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
import com.spring.backend.module.shared.util.OwnershipVerifier;
import com.spring.backend.module.user.user.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private ReviewMapper reviewMapper;

    @Mock
    private OwnershipVerifier ownershipVerifier;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private UserEntity guest;
    private PropertyEntity property;
    private BookingEntity booking;
    private ReviewEntity review;

    @BeforeEach
    void setUp() {
        guest = UserEntity.builder().id(1L).build();
        property = PropertyEntity.builder().id(2L).reviewScore(0.0).reviewCount(0).build();
        booking = BookingEntity.builder()
                .id(3L)
                .guest(guest)
                .property(property)
                .status(BookingStatus.COMPLETED)
                .build();
        review = ReviewEntity.builder()
                .id(4L)
                .booking(booking)
                .guest(guest)
                .property(property)
                .rating(4)
                .comment("Great stay")
                .build();
    }

    @Test
    @DisplayName("Returns all reviews as a mapped page")
    void getAllReview_returnsMappedPage() {
        Pageable pageable = Pageable.ofSize(5).withPage(1);
        when(reviewRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(review), pageable, 1));
        when(reviewMapper.toResponse(review)).thenReturn(mock(ReviewResponse.class));

        Page<ReviewResponse> result = reviewService.getAllReview(1, 5);

        assertThat(result.getContent()).hasSize(1);
        verify(reviewRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Returns property reviews after validating the property exists")
    void getAllReviewByProperty_propertyExists_returnsMappedPage() {
        Pageable pageable = Pageable.ofSize(5).withPage(0);
        ReviewResponse response = mock(ReviewResponse.class);
        when(propertyRepository.findById(property.getId())).thenReturn(Optional.of(property));
        when(reviewRepository.findByPropertyId(property.getId(), pageable))
                .thenReturn(new PageImpl<>(List.of(review), pageable, 1));
        when(reviewMapper.toResponse(review)).thenReturn(response);

        Page<ReviewResponse> result = reviewService.getAllReviewByProperty(property.getId(), 0, 5);

        assertThat(result.getContent()).containsExactly(response);
    }

    @Test
    @DisplayName("Throws when requesting reviews for a missing property")
    void getAllReviewByProperty_propertyMissing_throws() {
        when(propertyRepository.findById(property.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.getAllReviewByProperty(property.getId(), 0, 5))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(reviewRepository, never()).findByPropertyId(anyLong(), any(Pageable.class));
    }

    @Test
    @DisplayName("Returns the review associated with a booking")
    void getReviewByBooking_reviewExists_returnsMappedReview() {
        ReviewResponse response = mock(ReviewResponse.class);
        when(reviewRepository.findByBookingId(booking.getId())).thenReturn(review);
        when(reviewMapper.toResponse(review)).thenReturn(response);

        assertThat(reviewService.getReviewByBooking(booking.getId())).isSameAs(response);
    }

    @Test
    @DisplayName("Returns a guest's reviews as a mapped page")
    void getAllReviewByGuest_returnsMappedPage() {
        Pageable pageable = Pageable.ofSize(10).withPage(0);
        ReviewResponse response = mock(ReviewResponse.class);
        when(reviewRepository.findByGuestId(guest.getId(), pageable))
                .thenReturn(new PageImpl<>(List.of(review), pageable, 1));
        when(reviewMapper.toResponse(review)).thenReturn(response);

        Page<ReviewResponse> result = reviewService.getAllReviewByGuest(guest.getId(), 0, 10);

        assertThat(result.getContent()).containsExactly(response);
    }

    @Test
    @DisplayName("Creates a review for a completed booking and recalculates the property rating")
    void createReview_completedBooking_savesReviewAndRating() {
        ReviewCreateRequest request = mock(ReviewCreateRequest.class);
        ReviewResponse response = mock(ReviewResponse.class);
        when(request.getBookingId()).thenReturn(booking.getId());
        when(ownershipVerifier.getCurrentUser()).thenReturn(guest);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(reviewRepository.existsByBookingId(booking.getId())).thenReturn(false);
        when(reviewMapper.toEntity(request, booking, property, guest)).thenReturn(review);
        when(reviewRepository.save(review)).thenReturn(review);
        when(reviewRepository.findAllByPropertyId(property.getId())).thenReturn(List.of(review));
        when(reviewMapper.toResponse(review)).thenReturn(response);

        ReviewResponse result = reviewService.createReview(request);

        assertThat(result).isSameAs(response);
        assertThat(property.getReviewScore()).isEqualTo(4.0);
        assertThat(property.getReviewCount()).isEqualTo(1);
        verify(ownershipVerifier).verifyOwnershipOrAdmin(guest);
        verify(propertyRepository).save(property);
    }

    @Test
    @DisplayName("Rejects a second review for the same booking")
    void createReview_bookingAlreadyReviewed_throws() {
        ReviewCreateRequest request = mock(ReviewCreateRequest.class);
        when(request.getBookingId()).thenReturn(booking.getId());
        when(ownershipVerifier.getCurrentUser()).thenReturn(guest);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(reviewRepository.existsByBookingId(booking.getId())).thenReturn(true);

        assertThatThrownBy(() -> reviewService.createReview(request))
                .isInstanceOf(ReviewAlreadyExistsException.class);

        verify(reviewRepository, never()).save(any());
    }

    @Test
    @DisplayName("Rejects reviews for bookings that have not been completed")
    void createReview_bookingNotCompleted_throws() {
        ReviewCreateRequest request = mock(ReviewCreateRequest.class);
        booking.setStatus(BookingStatus.CONFIRMED);
        when(request.getBookingId()).thenReturn(booking.getId());
        when(ownershipVerifier.getCurrentUser()).thenReturn(guest);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> reviewService.createReview(request))
                .isInstanceOf(IllegalStateException.class);

        verify(reviewRepository, never()).save(any());
    }

    @Test
    @DisplayName("Updates supplied review fields and recalculates the property rating")
    void updateReview_fieldsProvided_savesAndRecalculatesRating() {
        ReviewUpdateRequest update = mock(ReviewUpdateRequest.class);
        when(update.getRating()).thenReturn(5);
        when(update.getComment()).thenReturn("Excellent");
        when(reviewRepository.findById(review.getId())).thenReturn(Optional.of(review));
        when(reviewRepository.save(review)).thenReturn(review);
        when(reviewRepository.findAllByPropertyId(property.getId())).thenReturn(List.of(review));
        when(reviewMapper.toResponse(review)).thenReturn(mock(ReviewResponse.class));

        reviewService.updateReview(review.getId(), update);

        assertThat(review.getRating()).isEqualTo(5);
        assertThat(review.getComment()).isEqualTo("Excellent");
        assertThat(property.getReviewScore()).isEqualTo(5.0);
        assertThat(property.getReviewCount()).isEqualTo(1);
        verify(ownershipVerifier).verifyOwnershipOrAdmin(guest);
    }

    @Test
    @DisplayName("Deletes a review and resets the property rating when no reviews remain")
    void deleteReviewById_lastReview_deletesAndResetsRating() {
        property.setReviewScore(4.0);
        property.setReviewCount(1);
        when(reviewRepository.findById(review.getId())).thenReturn(Optional.of(review));
        when(reviewRepository.findAllByPropertyId(property.getId())).thenReturn(List.of());

        reviewService.deleteReviewById(review.getId());

        assertThat(property.getReviewScore()).isZero();
        assertThat(property.getReviewCount()).isZero();
        verify(ownershipVerifier).verifyOwnershipOrAdmin(guest);
        verify(reviewRepository).delete(review);
        verify(propertyRepository).save(property);
    }
}
