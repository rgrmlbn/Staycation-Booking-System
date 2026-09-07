package com.spring.backend.module.property.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "amenities")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AmenityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

}
