package com.codecool.solarwatch.service;

import com.codecool.solarwatch.exceptionhandler.*;
import com.codecool.solarwatch.model.dto.*;
import com.codecool.solarwatch.model.entity.City;
import com.codecool.solarwatch.model.entity.SolarInfo;
import com.codecool.solarwatch.repository.CityRepository;
import com.codecool.solarwatch.repository.SolarInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
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
        String normalizedCityName = cityName.toLowerCase();
        String normalizedCountryName = country.toLowerCase();
        String normalizedStateName = (state == null) ? null : state.toLowerCase();

        Set<City> citiesInDb = retrieveCitiesFromDatabase(normalizedCityName, normalizedCountryName, normalizedStateName);

        if (citiesInDb.isEmpty()) {
            return processNewCityWithSolarInfo(normalizedCityName, normalizedCountryName, normalizedStateName, date);
        } else {
            return processExistingCitiesWithSolarInfo(citiesInDb, date);
        }
    }

    private Set<City> retrieveCitiesFromDatabase(String cityName, String country, String state) {
        if (state == null) {
            return cityRepository.findByNameAndCountryIgnoreCase(cityName, country);
        } else {
            return cityRepository.findByNameAndCountryAndStateIgnoreCase(cityName, country, state);
        }
    }

    private Set<SolarInfoDTO> processNewCityWithSolarInfo(String cityName, String country, String state, String date) {
        City city = fetchAndSaveCity(cityName, country, state);
        SolarInfo solarInfo = fetchAndSaveSunriseSunset(city, date);
        Set<SolarInfoDTO> solarInfoDTOSet = new HashSet<>();
        solarInfoDTOSet.add(convertToSolarInfoDTO(solarInfo));
        return solarInfoDTOSet;
    }

    private Set<SolarInfoDTO> processExistingCitiesWithSolarInfo(Set<City> cities, String date) {
        Set<SolarInfoDTO> solarInfoDTOSet = new HashSet<>();

        for (City city : cities) {
            Optional<SolarInfo> solarInfoOptional = solarInfoRepository.findByCityAndDate(city, date);

            if (solarInfoOptional.isPresent()) {
                solarInfoDTOSet.add(convertToSolarInfoDTO(solarInfoOptional.get()));
            } else {
                SolarInfo solarInfo = fetchAndSaveSunriseSunset(city, date);
                solarInfoDTOSet.add(convertToSolarInfoDTO(solarInfo));
            }
        }

        return solarInfoDTOSet;
    }

    @Transactional
    public void addSolarInfo(AddSolarInfoDTO solarInfoDTO) {
        Optional<City> cityOptional = findCity(solarInfoDTO);

        City city;
        if (cityOptional.isPresent()) {
            city = cityOptional.get();
        } else {
            city = saveNewCity(solarInfoDTO);
        }

        saveSolarInfo(solarInfoDTO, city);
    }

    private Optional<City> findCity(AddSolarInfoDTO solarInfoDTO) {
        if (StringUtils.hasText(solarInfoDTO.state())) {
            return cityRepository.findByNameAndCountryAndStateIgnoreCase(
                    solarInfoDTO.cityName(),
                    solarInfoDTO.country(),
                    solarInfoDTO.state()
            ).stream().findFirst();
        } else {
            return cityRepository.findByNameAndCountryIgnoreCase(
                    solarInfoDTO.cityName(),
                    solarInfoDTO.country()
            ).stream().findFirst();
        }
    }

    private City saveNewCity(AddSolarInfoDTO solarInfoDTO) {
        City newCity = new City();

        newCity.setPublicId(UUID.randomUUID());
        newCity.setName(solarInfoDTO.cityName().toLowerCase());
        newCity.setCountry(solarInfoDTO.country().toLowerCase());

        String state = solarInfoDTO.state() != null && !solarInfoDTO.state().isEmpty() ? solarInfoDTO.state().toLowerCase() : null;
        newCity.setState(state);

        newCity.setLatitude(solarInfoDTO.latitude());
        newCity.setLongitude(solarInfoDTO.longitude());

        return cityRepository.save(newCity);
    }

    private void saveSolarInfo(AddSolarInfoDTO solarInfoDTO, City city) {
        SolarInfo solarInfo = new SolarInfo();
        solarInfo.setPublicId(UUID.randomUUID());
        solarInfo.setCity(city);
        solarInfo.setDate(solarInfoDTO.date());
        solarInfo.setSunrise(solarInfoDTO.sunrise());
        solarInfo.setSunset(solarInfoDTO.sunset());

        solarInfoRepository.save(solarInfo);
    }

    private SolarInfoDTO convertToSolarInfoDTO(SolarInfo solarInfo) {
        City city = solarInfo.getCity();
        return new SolarInfoDTO(
                StringUtils.capitalize(city.getName()),
                capitalizeCountryCode(city.getCountry()),
                city.getState() != null ? StringUtils.capitalize(city.getState()) : null,
                solarInfo.getDate(),
                city.getLatitude(),
                city.getLongitude(),
                solarInfo.getSunrise(),
                solarInfo.getSunset()
        );
    }

    private String capitalizeCountryCode(String countryCode) {
        return StringUtils.hasText(countryCode) ? countryCode.toUpperCase() : null;
    }

    private City fetchAndSaveCity(String cityName, String country, String state) {
        String url = buildCityUrl(cityName, country, state);
        logger.info("URL: " + url);
        CityDTO[] cityArray = fetchCity(url);
        return saveAndReturnCity(validateCityResponse(cityArray, cityName, country, state));
    }

    private String buildCityUrl(String cityName, String country, String state) {
        if (country.equalsIgnoreCase("us")) {
            return String.format("https://api.openweathermap.org/geo/1.0/direct?q=%s,%s,%s&appid=%s", cityName, state, country, API_KEY);
        } else {
            return String.format("https://api.openweathermap.org/geo/1.0/direct?q=%s,%s&appid=%s", cityName, country, API_KEY);
        }
    }

    private CityDTO[] fetchCity(String url) {
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
        Optional<City> existingCity = cityRepository.findByNameAndCountryAndState(city.getName(), city.getCountry(), city.getState());
        if (existingCity.isPresent()) {
            return existingCity.get();
        }
        logger.info("City saved: {} ", city);
        return cityRepository.save(city);
    }

    private City convertToCity(CityDTO cityDTO) {
        City city = new City();
        city.setPublicId(UUID.randomUUID());
        city.setName(cityDTO.name().toLowerCase());
        city.setCountry(cityDTO.country().toLowerCase());
        if (cityDTO.state() != null) {
            city.setState(cityDTO.state().toLowerCase());
        } else {
            city.setState(null);
        }
        city.setLongitude(cityDTO.lon());
        city.setLatitude(cityDTO.lat());
        return city;
    }

    private String buildSunriseSunsetUrl(double latitude, double longitude, String date) {
        validateDate(date);
        return String.format("https://api.sunrise-sunset.org/json?lat=%s&lng=%s&date=%s", latitude, longitude, date);
    }

    public SolarInfo fetchAndSaveSunriseSunset(City city, String date) {
        String url = buildSunriseSunsetUrl(city.getLatitude(), city.getLongitude(), date);
        SunriseSunsetResultsResponse response = fetchSunriseSunset(url);
        return saveAndReturnSolarInfo(response, city, date);
    }

    private SunriseSunsetResultsResponse fetchSunriseSunset(String url) {
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
        Optional<SolarInfo> existingSolarInfo = solarInfoRepository.findByCityAndDate(city, date);

        if (existingSolarInfo.isPresent()) {
            return existingSolarInfo.get();
        }

        logger.info("Solar info saved: {} ", solarInfo);
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

    public Set<SolarInfoDTO> getAllSolarInfo() {
        Set<SolarInfoDTO> solarInfoDTOSet = new HashSet<>();
        Iterable<SolarInfo> solarInfos = solarInfoRepository.findAll();
        for (SolarInfo solarInfo : solarInfos) {
            solarInfoDTOSet.add(convertToSolarInfoDTO(solarInfo));
        }
        return solarInfoDTOSet;
    }
}
