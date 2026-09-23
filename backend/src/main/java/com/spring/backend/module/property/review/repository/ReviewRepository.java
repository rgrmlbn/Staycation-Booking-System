package com.spring.backend.module.property.review.repository;

import com.spring.backend.module.property.review.entity.ReviewEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {

    boolean existsByBookingId(Long bookingId);

    Page<ReviewEntity> findByPropertyId(Long propertyId, Pageable pageable);

    Page<ReviewEntity> findByBookingId(Long bookingId, Pageable pageable);

    Page<ReviewEntity> findByGuestId(Long guestId, Pageable pageable);

    List<ReviewEntity> findAllByPropertyId(Long propertyId);
}