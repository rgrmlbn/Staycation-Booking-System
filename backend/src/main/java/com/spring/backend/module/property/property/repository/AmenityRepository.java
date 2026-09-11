package com.spring.backend.module.property.property.repository;

import com.spring.backend.module.property.property.entity.AmenityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AmenityRepository extends JpaRepository<AmenityEntity, Long> {

    boolean existsByName(String name);
}
