package com.codecool.solarwatch.service;

import com.codecool.solarwatch.exceptionhandler.LocationNotFoundException;
import com.codecool.solarwatch.exceptionhandler.SolarInfoNotFoundException;
import com.codecool.solarwatch.model.city.City;
import com.codecool.solarwatch.model.city.CityDTO;
import com.codecool.solarwatch.model.solarinfo.SolarInfo;
import com.codecool.solarwatch.model.solarinfo.SolarInfoDTO;
import com.codecool.solarwatch.model.solarinfo.SolarInfoResultsDTO;
import com.codecool.solarwatch.repository.CityRepository;
import com.codecool.solarwatch.repository.SolarInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SolarInfoService implements UrlQueryValidator {

    private static final Logger logger = LoggerFactory.getLogger(SolarInfoService.class);
    private static final String API_KEY = "c19404c7cbefe404870319115a758b46";
    private final WebClient webClient;
    private final DateTimeFormatter dateFormatter;
    private final CityRepository cityRepository;
    private final SolarInfoRepository solarInfoRepository;

    public SolarInfoService(WebClient webClient, DateTimeFormatter dateFormatter, CityRepository cityRepository, SolarInfoRepository solarInfoRepository) {
        this.webClient = webClient;
        this.dateFormatter = dateFormatter;
        this.cityRepository = cityRepository;
        this.solarInfoRepository = solarInfoRepository;
    }

    public Set<SolarInfoDTO> getSolarInfo(String cityName, String date, int limit) {
        validateLimit(limit);
        Set<City> cityArray = cityRepository.findByName(StringUtils.capitalize(cityName));

        logger.info("City name to find: " + cityArray);

        if (cityArray.isEmpty() || cityArray.size() < limit) {
            cityArray = fetchCities(cityName, limit);
        }

        return fetchSunriseSunsetReports(cityArray, date, limit);
    }

    private Set<SolarInfoDTO> fetchSunriseSunsetReports(Set<City> cities, String date, int limit) {
        Set<SolarInfoDTO> reports = new HashSet<>();

        for (int i = 0; i < cities.size() && i < limit; i++) {
            City city = new ArrayList<>(cities).get(i);
            SolarInfo solarInfo = solarInfoRepository.findByCityAndDate(city, date);
            logger.info("Found city : " + solarInfo);

            if (solarInfo == null) {
                solarInfo = fetchSunriseSunset(city, date);
            }

            reports.add(new SolarInfoDTO(
                            city.getName(),
                            city.getCountry(),
                            city.getState(),
                            city.getLat(),
                            city.getLon(),
                            solarInfo.getSunrise(),
                            solarInfo.getSunset()
                    )
            );
        }

        return reports;
    }

    public Set<City> fetchCities(String cityName, int limit) {
        String url = String.format("https://api.openweathermap.org/geo/1.0/direct?q=%s&limit=%s&appid=%s", cityName, limit, API_KEY);
        Mono<CityDTO[]> response =
                webClient
                        .get()
                        .uri(url)
                        .retrieve()
                        .bodyToMono(CityDTO[].class)
                        .log();
        CityDTO[] cities = response.block();

        if (cities == null || cities.length == 0) {
            throw new LocationNotFoundException(cityName);
        }

        return saveCities(cityName, cities);
    }

    private Set<City> saveCities(String cityName, CityDTO[] cities) {

        Set<City> citySet = Arrays.stream(cities)
                .map(cityDTO -> {
                    City city = new City();
                    city.setName(cityDTO.name());
                    city.setCountry(cityDTO.country());
                    city.setState(cityDTO.state());
                    city.setLat(cityDTO.lat());
                    city.setLon(cityDTO.lon());
                    return city;
                })
                .collect(Collectors.toCollection(HashSet::new));

        Set<City> savedCities = new HashSet<>();

        for (City city : citySet) {
            if (city.getName().equalsIgnoreCase(cityName) || city.getName().toUpperCase().contains(cityName.toUpperCase())) {
                City existingCity = cityRepository.findByNameAndLatAndLon(city.getName(), city.getLat(), city.getLon());
                if (existingCity == null) {
                    City savedCity = cityRepository.save(city);
                    savedCities.add(savedCity);
                    logger.info("City saved to database: " + savedCity);
                } else {
                    savedCities.add(existingCity);
                    logger.info("City already exists in database: " + existingCity);
                }
            }
        }
        return savedCities;
    }

    public SolarInfo fetchSunriseSunset(City city, String date) {
        validateDate(date);

        String url = String.format("https://api.sunrise-sunset.org/json?lat=%s&lng=%s&date=%s", city.getLat(), city.getLon(), date);
        SolarInfoResultsDTO response = webClient
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(SolarInfoResultsDTO.class)
                .block();

        SolarInfo forecast = new SolarInfo();
        forecast.setDate(date);

        if (response != null) {
            forecast.setSunrise(response.results().sunrise());
            forecast.setSunset(response.results().sunset());
            forecast.setCity(city);

            return solarInfoRepository.save(forecast);
        } else {
            throw new SolarInfoNotFoundException(city.getName());
        }
    }

    @Override
    public void validateLimit(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be a positive integer");
        }
    }

    @Override
    public void validateDate(String dateStr) {
        LocalDate.parse(dateStr, dateFormatter);
    }
}
