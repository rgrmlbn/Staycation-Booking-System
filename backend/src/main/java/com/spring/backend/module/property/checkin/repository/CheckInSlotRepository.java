package com.spring.backend.module.property.checkin.repository;

import com.spring.backend.module.property.booking.entity.BookingEntity;
import com.spring.backend.module.property.checkin.entity.CheckInSlotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckInSlotRepository extends JpaRepository<CheckInSlotEntity, Long> {
}
