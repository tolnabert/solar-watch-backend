package com.codecool.solarwatch.service;

import com.codecool.solarwatch.exceptionhandler.LocationNotFoundException;
import com.codecool.solarwatch.exceptionhandler.NoTwilightDataException;
import com.codecool.solarwatch.model.LocationReport;
import com.codecool.solarwatch.model.TwilightResponse;
import com.codecool.solarwatch.model.TwilightReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class OpenWeatherService {

    private static final String API_KEY = "c19404c7cbefe404870319115a758b46";
    private final RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(OpenWeatherService.class);

    public OpenWeatherService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<LocationReport> getLocation(String city, int limit) {
        String url = String.format("https://api.openweathermap.org/geo/1.0/direct?q=%s&limit=%s&appid=%s", city, limit, API_KEY);
        LocationReport[] response = restTemplate.getForObject(url, LocationReport[].class);

        if (response != null && response.length > 0) {
            return Arrays.asList(Arrays.copyOf(response, Math.min(limit, response.length)));
        } else {
            throw new LocationNotFoundException(city);
        }
    }

    public TwilightReport getTwilight(LocationReport location) {
        String url = String.format("https://api.sunrise-sunset.org/json?lat=%s&lng=%s", location.lat(), location.lon());
        TwilightResponse response = restTemplate.getForObject(url, TwilightResponse.class);

        if (response != null && response.results() != null) {
            TwilightReport results = response.results();
            logger.info("Sunrise: {}, Sunset: {}", results.sunrise(), results.sunset());
            return new TwilightReport(results.sunrise(), results.sunset());
        } else {
            throw new NoTwilightDataException();
        }
    }
}
