package com.codecool.solarwatch.exceptionhandler;

public class NoTwilightDataException extends RuntimeException {
    public NoTwilightDataException() {
        super("No sunrise/sunset data found for the specified location");
    }
}
