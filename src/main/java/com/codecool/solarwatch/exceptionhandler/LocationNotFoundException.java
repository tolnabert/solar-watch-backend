package com.codecool.solarwatch.exceptionhandler;

public class LocationNotFoundException extends RuntimeException {
    public LocationNotFoundException(String city) {
        super("Location data not found for " + city);//should I use message and feed it from service?
    }
}
