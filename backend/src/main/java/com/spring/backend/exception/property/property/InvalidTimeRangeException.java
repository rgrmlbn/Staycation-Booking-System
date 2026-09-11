package com.spring.backend.exception.property.property;

public class InvalidTimeRangeException extends RuntimeException {
    public InvalidTimeRangeException() {
        super("Start time and end time must not be the same");
    }
}
