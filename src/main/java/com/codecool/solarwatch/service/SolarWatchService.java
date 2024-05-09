package com.codecool.solarwatch.service;

import com.codecool.solarwatch.exceptionhandler.CityNotFoundException;
import com.codecool.solarwatch.exceptionhandler.SolarInfoNotFoundException;
import com.codecool.solarwatch.model.entity.City;
import com.codecool.solarwatch.model.dto.CityDTO;
import com.codecool.solarwatch.model.entity.SolarInfo;
import com.codecool.solarwatch.model.dto.SolarInfoDTO;
import com.codecool.solarwatch.model.dto.SolarInfoResultsResponse;
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
import java.util.*;
import java.util.stream.Collectors;

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
            throw new CityNotFoundException(cityName);
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
                Optional<City> existingCityOptional = cityRepository.findByNameAndLatAndLon(city.getName(), city.getLat(), city.getLon());
                if (existingCityOptional.isPresent()) {
                    // If the city already exists, add it to the set of saved cities
                    savedCities.add(existingCityOptional.get());
                    logger.info("City already exists in database: " + existingCityOptional.get());
                } else {
                    // If the city does not exist, save it to the database and add it to the set of saved cities
                    City savedCity = cityRepository.save(city);
                    savedCities.add(savedCity);
                    logger.info("City saved to database: " + savedCity);
                }
            }
        }
        return savedCities;
    }

    public SolarInfo fetchSunriseSunset(City city, String date) {
        validateDate(date);

        String url = String.format("https://api.sunrise-sunset.org/json?lat=%s&lng=%s&date=%s", city.getLat(), city.getLon(), date);
        SolarInfoResultsResponse response = webClient
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(SolarInfoResultsResponse.class)
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

    public void addSolarInfo(SolarInfoDTO solarInfoDTO, String date) {
        validateDate(date);

        Set<City> cities = cityRepository.findByName(solarInfoDTO.name());

        if (cities == null) {
            City newCity = new City();
            newCity.setName(solarInfoDTO.name());
            newCity.setCountry(solarInfoDTO.country());
            newCity.setState(solarInfoDTO.state());
            newCity.setLon(solarInfoDTO.longitude());
            newCity.setLat(solarInfoDTO.latitude());

            cityRepository.save(newCity);

            SolarInfo solarInfo = new SolarInfo();
            solarInfo.setDate(date);
            solarInfo.setSunrise(solarInfoDTO.sunrise());
            solarInfo.setSunset(solarInfoDTO.sunset());
            solarInfo.setCity(newCity);
            solarInfoRepository.save(solarInfo);
        } else {
            throw new IllegalArgumentException("The value is already in the list.");
        }
    }

    public SolarInfoDTO updateSolarInfo(UUID id, SolarInfoDTO updatedSolarInfoDTO) {
        Optional<SolarInfo> optionalSolarInfo = solarInfoRepository.findByPublicId(id);
        if (optionalSolarInfo.isPresent()) {
            SolarInfo solarInfo = optionalSolarInfo.get();
            solarInfo.setSunrise(updatedSolarInfoDTO.sunrise());
            solarInfo.setSunset(updatedSolarInfoDTO.sunset());

            SolarInfo updatedSolarInfo = solarInfoRepository.save(solarInfo);

            return convertToSolarInfoDto(updatedSolarInfo);
        } else {
            throw new SolarInfoNotFoundException("SolarInfo not found with id: " + id);
        }
    }

    public SolarInfoDTO updateSolarInfo(UUID id, SolarInfoDTO updatedSolarInfoDTO, String date) {
        Optional<SolarInfo> optionalSolarInfo = solarInfoRepository.findByPublicId(id);
        if (optionalSolarInfo.isPresent()) {
            SolarInfo solarInfo = optionalSolarInfo.get();
            solarInfo.setDate(date);
            solarInfo.setSunrise(updatedSolarInfoDTO.sunrise());
            solarInfo.setSunset(updatedSolarInfoDTO.sunset());

            SolarInfo updatedSolarInfo = solarInfoRepository.save(solarInfo);

            return convertToSolarInfoDto(updatedSolarInfo);
        } else {
            throw new SolarInfoNotFoundException("SolarInfo not found with id: " + id);
        }
    }

    private SolarInfoDTO convertToSolarInfoDto(SolarInfo solarInfo) {
        return new SolarInfoDTO(
                solarInfo.getCity().getName(),
                solarInfo.getCity().getCountry(),
                solarInfo.getCity().getState(),
                solarInfo.getCity().getLat(),
                solarInfo.getCity().getLon(),
                solarInfo.getSunrise(),
                solarInfo.getSunset()
        );
    }

    public CityDTO updateCity(String cityName, UUID id, CityDTO updatedCityDTO) {
        Optional<City> optionalCity = cityRepository.findByNameAndLatAndLon(cityName, updatedCityDTO.lat(), updatedCityDTO.lon());
        if (optionalCity.isPresent()) {
            City city = optionalCity.get();
            city.setName(updatedCityDTO.name());
            city.setCountry(updatedCityDTO.country());
            city.setState(updatedCityDTO.state());
            city.setLat(updatedCityDTO.lat());
            city.setLon(updatedCityDTO.lon());

            City updatedCity = cityRepository.save(city);

            return convertToCityDto(updatedCity);
        } else {
            throw new CityNotFoundException("City not found with name: " + cityName + " and ID: " + id);
        }
    }

    private CityDTO convertToCityDto(City city) {
        return new CityDTO(
                city.getName(),
                city.getCountry(),
                city.getState(),
                city.getLat(),
                city.getLon()
        );
    }

    public boolean deleteSolarInfo(UUID id) {
        Optional<SolarInfo> solarInfoOptional = solarInfoRepository.findByPublicId(id);
        if (solarInfoOptional.isPresent()) {
            solarInfoRepository.deleteByPublicId(id);
            return true;
        } else {
            return false;
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
