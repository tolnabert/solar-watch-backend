package com.codecool.solarwatch.exceptionhandler;

public class CityNotFoundExternalException extends RuntimeException {
    public CityNotFoundExternalException() {
        super("Location data not found for city");
    }
}
