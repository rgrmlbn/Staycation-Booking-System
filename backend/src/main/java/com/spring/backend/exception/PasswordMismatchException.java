package com.spring.backend.exception;

public class PasswordMismatchException extends RuntimeException {
    public PasswordMismatchException() {
        super("Passwords don't match");
    }
}
