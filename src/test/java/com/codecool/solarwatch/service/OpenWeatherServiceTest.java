package com.codecool.solarwatch.service;

import com.codecool.solarwatch.model.LocationReport;
import com.codecool.solarwatch.model.SunriseSunsetResponse;
import com.codecool.solarwatch.model.SunriseSunsetResults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class OpenWeatherServiceTest {

    @Mock
    private RestTemplate restTemplate;
    private OpenWeatherService openWeatherService;


    @BeforeEach
    void init() {
        MockitoAnnotations.initMocks(this);
        openWeatherService = new OpenWeatherService(restTemplate);
    }

    @Test
    void testGetLocation() {
        LocationReport[] mockResponse = {new LocationReport(47.4979937, 19.0403594)};
        when(restTemplate.getForObject(any(String.class), any(Class.class))).thenReturn(mockResponse);

        LocationReport location = openWeatherService.getLocation("Budapest");

        assertEquals(47.4979937, location.lat());
        assertEquals(19.0403594, location.lon());
    }

    @Test
    void testGetTwilight() {
        SunriseSunsetResponse mockResponse = new SunriseSunsetResponse(new SunriseSunsetResults("4:09:54 AM", "5:22:19 PM"));
        when(restTemplate.getForObject(any(String.class), any(Class.class))).thenReturn(mockResponse);

        LocationReport location = new LocationReport(47.4979937, 19.0403594);
        SunriseSunsetResults results = openWeatherService.getTwilight(location);

        assertEquals("4:09:54 AM", results.sunrise());
        assertEquals("5:22:19 PM", results.sunset());
    }
}