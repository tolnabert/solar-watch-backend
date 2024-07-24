package com.codecool.solarwatch.model.dto;

public record SolarInfoDTO(String name, String country, String state, String date, double latitude, double longitude,
                           String sunrise,
                           String sunset) {
}
