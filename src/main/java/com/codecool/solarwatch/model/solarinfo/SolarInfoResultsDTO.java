package com.codecool.solarwatch.model.solarinfo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SolarInfoResultsDTO(SolarInfoDTO results) {
}
