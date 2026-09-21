package com.spring.backend.module.property.review.entity;

import com.spring.backend.module.property.booking.entity.BookingEntity;
import com.spring.backend.module.property.property.entity.PropertyEntity;
import com.spring.backend.module.user.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // One review per booking — unique enforces that at the DB level too
    @OneToOne
    @JoinColumn(name = "booking_id", nullable = false, unique = true)
    private BookingEntity booking;

    // Denormalized so we can query/aggregate reviews by property
    // without joining through booking every time.
    @ManyToOne
    @JoinColumn(name = "property_id", nullable = false)
    private PropertyEntity property;

    @ManyToOne
    @JoinColumn(name = "guest_id", nullable = false)
    private UserEntity guest;

    @Column(nullable = false)
    private Integer rating;

    @Column(columnDefinition = "TEXT")
    private String comment;
}
