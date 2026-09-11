package com.spring.backend.module.property.booking.entity;

import com.spring.backend.module.property.property.entity.PropertyEntity;
import com.spring.backend.module.shared.entity.BaseEntity;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

public class BookingEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "property_id", nullable = false)
    private PropertyEntity property;

    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    private LocalTime checkInTime;
    private LocalTime checkOutTime;

}
