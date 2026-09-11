package com.spring.backend.module.property.property.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CheckInSlotUpdate {

    private LocalTime startTime;
    private LocalTime endTime;
}
