package com.codecool.solarwatch.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CityDTO(String name, String country, String state, double lon, double lat) {
}
