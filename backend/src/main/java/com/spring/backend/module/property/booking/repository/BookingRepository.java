package com.spring.backend.module.property.booking.repository;

import com.spring.backend.module.property.booking.entity.BookingEntity;
import com.spring.backend.module.property.booking.enums.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, Long> {

    Page<BookingEntity> findByGuestId(Long guestId, Pageable pageable);
    Page<BookingEntity>findByPropertyId(Long propertyId, Pageable pageable);
    BookingEntity findByStatus(BookingStatus status);

}
