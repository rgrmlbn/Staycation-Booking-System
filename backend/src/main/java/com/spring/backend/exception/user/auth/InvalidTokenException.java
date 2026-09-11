package com.spring.backend.exception.user.auth;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException( ) {
        super("Invalid token");
    }
}
