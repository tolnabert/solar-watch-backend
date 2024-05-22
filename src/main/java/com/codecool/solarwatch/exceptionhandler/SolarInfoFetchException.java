package com.codecool.solarwatch.exceptionhandler;

public class SolarInfoFetchException extends RuntimeException {
    public SolarInfoFetchException(Throwable e) {
        super("Error fetching solar info data from external API" + e);
    }
}
