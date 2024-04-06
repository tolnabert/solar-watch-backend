package com.codecool.solarwatch.service;

import com.codecool.solarwatch.model.LocationReport;
import com.codecool.solarwatch.model.SunriseSunsetResponse;
import com.codecool.solarwatch.model.SunriseSunsetResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OpenWeatherService {

    private static final String API_KEY = "c19404c7cbefe404870319115a758b46";
    private final RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(OpenWeatherService.class);

    public OpenWeatherService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public LocationReport getLocation(String city) {
        String url = String.format("https://api.openweathermap.org/geo/1.0/direct?q=%s&appid=%s", city, API_KEY);
        LocationReport[] response = restTemplate.getForObject(url, LocationReport[].class);

        if (response != null && response.length > 0) {
            return response[0];
        } else {
            throw new RuntimeException("No location data found for the specified city: " + city);
        }
    }

    public SunriseSunsetResults getTwilight(LocationReport location) {

        String url = String.format("https://api.sunrise-sunset.org/json?lat=%s&lng=%s", location.lat(), location.lon());
        SunriseSunsetResponse response = restTemplate.getForObject(url, SunriseSunsetResponse.class);

        if (response != null && response.results() != null) {
            SunriseSunsetResults results = response.results();
            logger.info("Sunrise: {}, Sunset: {}", results.sunrise(), results.sunset());
            return new SunriseSunsetResults(results.sunrise(), results.sunset());
        } else {
            throw new RuntimeException("No sunrise/sunset data found for the specified location.");
        }
    }
}
