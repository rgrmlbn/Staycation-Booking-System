package com.spring.backend.exception.property.rating;

public class ReviewAlreadyExistsException extends RuntimeException {
    public ReviewAlreadyExistsException() {
        super("A review already exists for this booking");
    }
}
