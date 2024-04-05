package com.codecool.solarwatch.service;

import com.codecool.solarwatch.model.LocationReport;
import com.codecool.solarwatch.model.TwilightReport;
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
        LocationReport response = restTemplate.getForObject(url, LocationReport.class);

        return new LocationReport(response.lat(), response.lon());
    }

    public TwilightReport getTwilight(LocationReport location) {

        String url = String.format("https://api.sunrise-sunset.org/json?lat=%s&lng=%s", location.lat(), location.lon());
        TwilightReport response = restTemplate.getForObject(url, TwilightReport.class);

        logger.info("Response from Open Weather API: {}", response);

        return new TwilightReport(response.sunrise(), response.sunset());
    }
}
