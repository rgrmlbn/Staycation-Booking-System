package com.spring.backend.exception;

public class IncorrectCurrentPasswordException extends RuntimeException {
    public IncorrectCurrentPasswordException() {
        super("Incorrect current password");
    }
}
