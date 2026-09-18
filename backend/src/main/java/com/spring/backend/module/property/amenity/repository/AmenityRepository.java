package com.spring.backend.module.property.amenity.repository;

import com.spring.backend.module.property.amenity.entity.AmenityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AmenityRepository extends JpaRepository<AmenityEntity, Long> {

    boolean existsByName(String name);
}
