package com.spring.backend.module.property.property.repository;

import com.spring.backend.module.property.property.entity.PropertyEntity;
import com.spring.backend.module.property.property.enums.PropertyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PropertyRepository extends JpaRepository<PropertyEntity, Long> {

    Page<PropertyEntity> findByTitleContainingIgnoreCase(
            String title,
            Pageable pageable
    );

    Page<PropertyEntity> findByStatus(
            PropertyStatus status,
            Pageable pageable
    );

    Page<PropertyEntity> findAllByUserId(Long userId, Pageable pageable);

    Page<PropertyEntity> findAllByUserIdAndTitleContainingIgnoreCase(
            Long userId,
            String title,
            Pageable pageable
    );

}
