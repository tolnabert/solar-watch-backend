package com.codecool.solarwatch.exceptionhandler;

public class SolarInfoNotFoundException extends RuntimeException {
    public SolarInfoNotFoundException(String cityName) {
        super("No sunrise/sunset data found for the specified location of " + cityName);
    }
}
