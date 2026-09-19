package com.spring.backend.exception.property.booking;

public class BookingAlreadyExistsException extends RuntimeException {
    public BookingAlreadyExistsException() {
        super("Property is already booked for the selected time");
    }
}
