package com.spring.backend.module.property.checkin.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@Builder
public class CheckInSlotResponse {

    private Long id;
    private LocalTime startTime;
    private LocalTime endTime;
    private Long durationMinutes;
    private Double price;
}