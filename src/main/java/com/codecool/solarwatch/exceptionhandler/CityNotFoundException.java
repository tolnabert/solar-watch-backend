package com.codecool.solarwatch.exceptionhandler;

public class CityNotFoundException extends RuntimeException {
    public CityNotFoundException(String city) {
        super("Location data not found for " + city);
    }
}
