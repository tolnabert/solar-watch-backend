package com.codecool.solarwatch.controller;

import com.codecool.solarwatch.model.TwilightReport;
import com.codecool.solarwatch.service.OpenWeatherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LocationController {

    private final OpenWeatherService openWeatherService;

    public LocationController(OpenWeatherService openWeatherService) {
        this.openWeatherService = openWeatherService;
    }

    @GetMapping("/twilight")
    public ResponseEntity<?> getTwilight(@RequestParam String city) {

        if (city == null) {
            return ResponseEntity.badRequest().body("City must be given");
        }

        TwilightReport twilight = openWeatherService.getTwilight(city);

        return ResponseEntity.ok(twilight);

    }
}
