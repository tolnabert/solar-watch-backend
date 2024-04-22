package com.codecool.solarwatch.controller;

import com.codecool.solarwatch.model.LocationReport;
import com.codecool.solarwatch.model.TwilightReport;
import com.codecool.solarwatch.service.OpenWeatherService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class LocationController {

    private final OpenWeatherService openWeatherService;

    public LocationController(OpenWeatherService openWeatherService) {
        this.openWeatherService = openWeatherService;
    }

    @GetMapping("/twilight/{city}")
    public List<TwilightReport> getTwilight(@PathVariable String city,
                                            @RequestParam(defaultValue = "1") int limit) {

        List<LocationReport> locations = openWeatherService.getLocation(city, limit);
        List<TwilightReport> twilightReports = locations.stream()
                .map(openWeatherService::getTwilight)
                .collect(Collectors.toList());

        return twilightReports;
    }
}
