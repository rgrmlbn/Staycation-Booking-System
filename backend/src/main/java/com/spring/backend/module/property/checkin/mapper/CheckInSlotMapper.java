package com.spring.backend.module.property.checkin.mapper;

import com.spring.backend.module.property.checkin.dto.request.CheckInSlotCreateRequest;
import com.spring.backend.module.property.checkin.dto.response.CheckInSlotResponse;
import com.spring.backend.module.property.checkin.entity.CheckInSlotEntity;
import com.spring.backend.module.property.property.entity.PropertyEntity;
import org.springframework.stereotype.Component;

@Component
public class CheckInSlotMapper {

    public CheckInSlotEntity toCheckInSlotEntity(CheckInSlotCreateRequest request, PropertyEntity property) {
        return CheckInSlotEntity.builder()
                .startTime(request.getStartTime())
                .durationHours(request.getDurationHours())
                .price(request.getPrice())
                .property(property)
                .build();
    }

    public CheckInSlotResponse toCheckInSlotResponse(CheckInSlotEntity entity) {
        return CheckInSlotResponse.builder()
                .id(entity.getId())
                .startTime(entity.getStartTime())
                .durationHours(entity.getDurationHours())
                .price(entity.getPrice())
                .build();
    }
}