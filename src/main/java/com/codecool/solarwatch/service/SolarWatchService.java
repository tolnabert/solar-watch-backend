package com.codecool.solarwatch.service;

import com.codecool.solarwatch.exceptionhandler.*;
import com.codecool.solarwatch.model.dto.SunriseSunsetDTO;
import com.codecool.solarwatch.model.entity.City;
import com.codecool.solarwatch.model.dto.CityDTO;
import com.codecool.solarwatch.model.entity.SolarInfo;
import com.codecool.solarwatch.model.dto.SolarInfoDTO;
import com.codecool.solarwatch.model.dto.SunriseSunsetResultsResponse;
import com.codecool.solarwatch.repository.CityRepository;
import com.codecool.solarwatch.repository.SolarInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class SolarWatchService implements UrlQueryValidator {

    private static final Logger logger = LoggerFactory.getLogger(SolarWatchService.class);
    private static final String API_KEY = "c19404c7cbefe404870319115a758b46";
    private final WebClient webClient;
    private final DateTimeFormatter dateFormatter;
    private final CityRepository cityRepository;
    private final SolarInfoRepository solarInfoRepository;

    public SolarWatchService(WebClient webClient, DateTimeFormatter dateFormatter, CityRepository cityRepository, SolarInfoRepository solarInfoRepository) {
        this.webClient = webClient;
        this.dateFormatter = dateFormatter;
        this.cityRepository = cityRepository;
        this.solarInfoRepository = solarInfoRepository;
    }

    public Set<SolarInfoDTO> getSolarInfo(String cityName, String country, String state, String date) {
        String lowerCaseCityName = cityName.toLowerCase();
        String lowerCaseState = (state == null) ? null : state.toLowerCase();

        Set<City> citySet;
        if (lowerCaseState == null) {
            citySet = cityRepository.findByNameAndCountry(lowerCaseCityName, country);
        } else {
            citySet = cityRepository.findByNameAndCountryAndState(lowerCaseCityName, country, state);
        }

        Set<SolarInfoDTO> solarInfoDTOSet = new HashSet<>();

        if (citySet.isEmpty()) {
            City city = fetchCity(cityName, country, state);
            SolarInfo solarInfo = fetchSunriseSunset(city, date);
            solarInfoDTOSet.add(convertToSolarInfoDTO(solarInfo));
        } else {
            for (City city : citySet) {
                Optional<SolarInfo> solarInfoOptional = solarInfoRepository.findByCityAndDate(city, date);

                if (solarInfoOptional.isPresent()) {
                    solarInfoDTOSet.add(convertToSolarInfoDTO(solarInfoOptional.get()));
                } else {
                    SolarInfo solarInfo = fetchSunriseSunset(city, date);
                    solarInfoDTOSet.add(convertToSolarInfoDTO(solarInfo));
                }
            }
        }

        return solarInfoDTOSet;
    }

    private SolarInfoDTO convertToSolarInfoDTO(SolarInfo solarInfo) {
        City city = solarInfo.getCity();
        return new SolarInfoDTO(
                city.getName(),
                city.getCountry(),
                city.getState(),
                city.getLatitude(),
                city.getLongitude(),
                solarInfo.getSunrise(),
                solarInfo.getSunset()
        );
    }

    private City fetchCity(String cityName, String country, String state) {
        String url = buildCityUrl(cityName, country, state);
        logger.info("URL: " + url);
        CityDTO[] cityArray = fetchCityData(url);
        return saveAndReturnCity(validateCityResponse(cityArray, cityName, country, state));
    }

    private String buildCityUrl(String cityName, String country, String state) {
        if (country.equals("US")) {
            return String.format("https://api.openweathermap.org/geo/1.0/direct?q=%s,%s,%s&appid=%s", cityName, country, state, API_KEY);
        } else {
            return String.format("https://api.openweathermap.org/geo/1.0/direct?q=%s,%s&appid=%s", cityName, country, API_KEY);
        }
    }

    private CityDTO[] fetchCityData(String url) {
        Mono<CityDTO[]> responseMono = webClient
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(CityDTO[].class)
                .onErrorResume(e -> Mono.error(new CityFetchException(e)));

        CityDTO[] cityArray = responseMono.block();
        if (cityArray == null || cityArray.length <= 0) {
            throw new CityNotFoundExternalException();
        } else {
            logger.info("City data fetched from external API");
            return cityArray;
        }
    }

    private CityDTO validateCityResponse(CityDTO[] cityArray, String cityName, String country, String state) {
        if (cityArray == null || cityArray.length == 0) {
            throw new CityNotFoundInternalException(cityName, country, state);
        }
        logger.info("City array: " + Arrays.toString(cityArray));
        return cityArray[0];
    }

    private City saveAndReturnCity(CityDTO cityDTO) {
        City city = convertToCity(cityDTO);
        logger.info("City saved: {} ", city);
        return cityRepository.save(city);
    }

    private City convertToCity(CityDTO cityDTO) {
        City city = new City();
        city.setPublicId(UUID.randomUUID());
        city.setName(cityDTO.name());
        city.setCountry(cityDTO.country());
        city.setState(cityDTO.state());
        city.setLongitude(cityDTO.lon());
        city.setLatitude(cityDTO.lat());
        return city;
    }

    private String buildSunriseSunsetUrl(double latitude, double longitude, String date) {
        validateDate(date);
        return String.format("https://api.sunrise-sunset.org/json?lat=%s&lng=%s&date=%s", latitude, longitude, date);
    }

    public SolarInfo fetchSunriseSunset(City city, String date) {
        String url = buildSunriseSunsetUrl(city.getLatitude(), city.getLongitude(), date);
        SunriseSunsetResultsResponse response = fetchSunriseSunsetData(url);
        return saveAndReturnSolarInfo(response, city, date);
    }

    private SunriseSunsetResultsResponse fetchSunriseSunsetData(String url) {
        Mono<SunriseSunsetResultsResponse> responseMono = webClient
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(SunriseSunsetResultsResponse.class)
                .onErrorResume(e -> Mono.error(new SolarInfoFetchException(e)));

        SunriseSunsetResultsResponse response = responseMono.block();
        if (response == null || response.results() == null) {
            throw new SolarInfoNotFoundException("Sunrise/sunset not found for the provided coordinates.");
        }
        logger.info("Sunrise/sunset data fetched from external API");
        return response;
    }

    private SolarInfo saveAndReturnSolarInfo(SunriseSunsetResultsResponse response, City city, String date) {
        SolarInfo solarInfo = convertToSolarInfo(response.results(), city, date);
        logger.info("City saved: {} ", solarInfo);
        return solarInfoRepository.save(solarInfo);
    }

    private SolarInfo convertToSolarInfo(SunriseSunsetDTO sunriseSunsetDTO, City city, String date) {
        SolarInfo solarInfo = new SolarInfo();
        solarInfo.setPublicId(UUID.randomUUID());
        solarInfo.setDate(date);
        solarInfo.setSunrise(sunriseSunsetDTO.sunrise());
        solarInfo.setSunset(sunriseSunsetDTO.sunset());
        solarInfo.setCity(city);
        return solarInfo;
    }

    @Override
    public void validateDate(String dateStr) {
        LocalDate.parse(dateStr, dateFormatter);
    }
}
