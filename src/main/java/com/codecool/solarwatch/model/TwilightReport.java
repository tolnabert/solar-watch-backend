package com.codecool.solarwatch.model;

import java.time.LocalTime;

public record TwilightReport(LocalTime sunrise, LocalTime sunset) {
}
