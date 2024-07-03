package com.codecool.solarwatch.model.dto;

public record AddSolarInfoDTO(
        String cityName,
        String country,
        String state,
        String date,
        double latitude,
        double longitude,
        String sunrise,
        String sunset
) {
}
