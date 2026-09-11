package com.spring.backend.exception.property.property;

public class OverlappingTimeSlotException extends RuntimeException {
    public OverlappingTimeSlotException() {
        super("Check-in slots cannot overlap");
    }
}
