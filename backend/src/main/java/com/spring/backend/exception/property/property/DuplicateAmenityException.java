package com.spring.backend.exception.property.property;

public class DuplicateAmenityException extends RuntimeException {
    public DuplicateAmenityException() {
        super("An amenity with this name already exists");
    }
}
