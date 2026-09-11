package com.spring.backend.exception.property.property;

public class DuplicateImageException extends RuntimeException {
    public DuplicateImageException() {
        super("Duplicate image URLs are not allowed");
    }
}
