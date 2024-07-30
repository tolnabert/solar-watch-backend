package com.codecool.solarwatch.exceptionhandler;

public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}