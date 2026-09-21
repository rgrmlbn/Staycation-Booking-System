package com.spring.backend.module.property.booking.entity;

import com.spring.backend.module.property.booking.enums.BookingStatus;
import com.spring.backend.module.property.checkin.entity.CheckInSlotEntity;
import com.spring.backend.module.property.property.entity.PropertyEntity;
import com.spring.backend.module.shared.entity.BaseEntity;
import com.spring.backend.module.user.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "property_id", nullable = false)
    private PropertyEntity property;

    @ManyToOne
    @JoinColumn(name = "guest_id", nullable = false)
    private UserEntity guest;

    @ManyToOne
    @JoinColumn(name = "check_in_slot_id", nullable = false)
    private CheckInSlotEntity checkInSlot;

    @Column(nullable = false)
    private LocalDateTime checkInDateTime;

    @Column(nullable = false)
    private LocalDateTime checkOutDateTime;

    @Column(nullable = false)
    private Integer numberOfGuests;

    @Column(nullable = false)
    private Double totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private BookingStatus status = BookingStatus.PENDING;

    private String statusReason;

}