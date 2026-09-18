package com.spring.backend.module.property.checkin.entity;

import com.spring.backend.module.property.property.entity.PropertyEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Table(name = "check_in_slots")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckInSlotEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Column(nullable = false)
    private Double price;

    @ManyToOne
    @JoinColumn(name = "property_id", nullable = false)
    private PropertyEntity property;
}