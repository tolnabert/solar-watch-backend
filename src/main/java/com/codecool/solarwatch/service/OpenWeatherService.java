package com.codecool.solarwatch.service;

import com.codecool.solarwatch.model.OpenWeatherReport;
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

    public TwilightReport getTwilight(String city) {

        String url = String.format("https://api.openweathermap.org/geo/1.0/direct?q=%s&appid=%s", city, API_KEY);
        OpenWeatherReport openWeatherResponse = restTemplate.getForObject(url, OpenWeatherReport.class);

        String sunriseSunsetUrl = String.format("https://api.sunrise-sunset.org/json?lat=%s&lng=%s", openWeatherResponse.lat(), openWeatherResponse.lon());
        TwilightReport response = restTemplate.getForObject(sunriseSunsetUrl, TwilightReport.class);

        logger.info("Response from Open Weather API: {}", response);

        return new TwilightReport(response.sunrise(), response.sunset());
    }
}
