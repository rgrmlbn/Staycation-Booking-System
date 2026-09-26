package com.spring.backend.module.property.review.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ReviewUpdateRequest {

    @Min(1)
    @Max(5)
    private Integer rating;

    @Size(min = 5, max = 2000)
    private String comment;
}