package com.codecool.solarwatch.exceptionhandler;

public class CityFetchException extends RuntimeException {
    public CityFetchException(Throwable e) {
        super("Error fetching city data data from external API", e);
    }
}
