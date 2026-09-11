package com.spring.backend.module.property.property.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@Builder
public class CheckInSlotResponse {

    private Long id;
    private LocalTime startTime;
    private LocalTime endTime;
}
