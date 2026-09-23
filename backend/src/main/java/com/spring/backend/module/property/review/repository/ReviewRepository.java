package com.spring.backend.module.property.review.repository;

import com.spring.backend.module.property.review.entity.ReviewEntity;
import org.springdoc.core.converters.models.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
    Page<ReviewEntity> findAllByPropertyId(Long propertyId, Pageable pageable);
}
