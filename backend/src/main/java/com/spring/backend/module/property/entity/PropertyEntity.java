package com.spring.backend.module.property.entity;

import com.spring.backend.module.property.enums.PropertyStatus;
import com.spring.backend.module.user.user.entity.UserEntity;
import com.spring.backend.module.shared.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "properties")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PropertyEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "host_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false)
    @Size(min = 5, max = 30, message = "Title should be between 5 and 30 characters")
    private String title;

    @Column(nullable = false)
    @Size(min = 10, max = 100, message = "Description should be between 10 and 100 characters")
    private String description;

    @Column(nullable = false)
    @Min(value = 350, message = "Price per night should be 350 or more")
    private Double pricePerNight;

    @Column(nullable = false)
    @Min(value = 1, message = "Number of bedrooms should be 1 or more")
    private Integer bedrooms;

    @Column(nullable = false)
    @Min(value = 1, message = "Number of bathrooms should be 1 or more")
    private Integer bathrooms;

    @Column(nullable = false)
    private Boolean airConditioning;

    @Column(nullable = false)
    @Size(min = 10, max = 100, message = "Address should be between 10 and 100 characters")
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PropertyStatus status = PropertyStatus.AVAILABLE;

//    // Images
//    @OneToMany(
//            mappedBy = "property",
//            cascade = CascadeType.ALL,
//            orphanRemoval = true
//    )
//    @Builder.Default
//    private List<PropertyImageEntity> images = new ArrayList<>();
//
//    // Amenities
//    @ManyToMany
//    @JoinTable(
//            name = "property_amenities",
//            joinColumns = @JoinColumn(name = "property_id"),
//            inverseJoinColumns = @JoinColumn(name = "amenity_id")
//    )
//    @Builder.Default
//    private List<AmenityEntity> amenities = new ArrayList<>();
}