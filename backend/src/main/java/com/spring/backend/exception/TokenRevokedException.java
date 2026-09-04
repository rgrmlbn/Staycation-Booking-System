package com.spring.backend.exception;

public class TokenRevokedException extends RuntimeException {
    public TokenRevokedException() {
        super("Token has been revoked");
    }
}
