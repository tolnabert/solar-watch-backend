package com.codecool.solarwatch.exceptionhandler;

public class CityNotFoundInternalException extends RuntimeException {
    public CityNotFoundInternalException(String city, String country, String state) {
        super("Location data not found for city: " + city + ", country: " + country + (state != null ? "state: " + state : ""));
    }
}
