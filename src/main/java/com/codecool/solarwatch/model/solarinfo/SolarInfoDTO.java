package com.codecool.solarwatch.model.solarinfo;

public record SolarInfoDTO(String name, String country, String state, double latitude, double longitude,
                           String sunrise,
                           String sunset) {
}
