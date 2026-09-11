package com.spring.backend.exception.user.user;

public class IncorrectCurrentPasswordException extends RuntimeException {
    public IncorrectCurrentPasswordException() {
        super("Incorrect current password");
    }
}
