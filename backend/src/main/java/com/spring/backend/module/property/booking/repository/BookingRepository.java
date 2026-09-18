package com.spring.backend.module.property.booking.repository;

import com.spring.backend.module.property.booking.entity.BookingEntity;
import com.spring.backend.module.property.booking.enums.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, Long> {

    Page<BookingEntity> findByGuestId(Long guestId, Pageable pageable);
    Page<BookingEntity> findByPropertyId(Long propertyId, Pageable pageable);
    BookingEntity findByStatus(BookingStatus status);

    @Query("""
        SELECT COUNT(b) > 0 FROM BookingEntity b
        WHERE b.property.id = :propertyId
        AND b.status <> 'CANCELLED'
        AND (
            (b.checkInDate < :checkOutDate
                OR (b.checkInDate = :checkOutDate AND b.checkInTime < :checkOutTime))
            AND
            (b.checkOutDate > :checkInDate
                OR (b.checkOutDate = :checkInDate AND b.checkOutTime > :checkInTime))
        )
    """)
    boolean existsOverlappingBooking(@Param("propertyId") Long propertyId,
                                     @Param("checkInDate") LocalDate checkInDate,
                                     @Param("checkInTime") LocalTime checkInTime,
                                     @Param("checkOutDate") LocalDate checkOutDate,
                                     @Param("checkOutTime") LocalTime checkOutTime);
}