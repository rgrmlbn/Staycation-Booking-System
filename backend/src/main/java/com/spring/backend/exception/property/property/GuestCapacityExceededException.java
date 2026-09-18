package com.spring.backend.exception.property.property;

public class GuestCapacityExceededException extends RuntimeException {

    public GuestCapacityExceededException() {
        super("The number of guests exceeds the property's maximum capacity.");
    }
}