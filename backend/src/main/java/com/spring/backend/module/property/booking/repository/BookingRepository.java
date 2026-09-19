package com.spring.backend.module.property.booking.repository;

import com.spring.backend.module.property.booking.entity.BookingEntity;
import com.spring.backend.module.property.booking.enums.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, Long> {

    Page<BookingEntity> findByGuestId(Long guestId, Pageable pageable);
    Page<BookingEntity> findByPropertyId(Long propertyId, Pageable pageable);

    // Mirrors findByPropertyId, but scoped to properties owned by a given user
    // (a host, in this context). PropertyEntity's owner field is "user" — see
    // PropertyServiceImpl's use of property.getUser() — so this traverses
    // booking -> property -> user -> id.
    Page<BookingEntity> findByProperty_UserId(Long hostId, Pageable pageable);

    // Unpaged variant, used for the overlap check in BookingServiceImpl: the
    // service pulls every booking for the property and filters by status and
    // time range itself with plain if statements, the same way
    // PropertyServiceImpl checks things like duplicate images/amenities in
    // Java rather than pushing that logic into the query.
    List<BookingEntity> findByPropertyId(Long propertyId);

    BookingEntity findByStatus(BookingStatus status);
}