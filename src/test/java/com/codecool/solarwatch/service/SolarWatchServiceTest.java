package com.codecool.solarwatch.service;

import com.codecool.solarwatch.model.dto.SolarInfoDTO;
import com.codecool.solarwatch.model.entity.City;
import com.codecool.solarwatch.model.entity.SolarInfo;
import com.codecool.solarwatch.repository.CityRepository;
import com.codecool.solarwatch.repository.SolarInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SolarWatchServiceTest {

    @Mock
    private CityRepository cityRepository;
    @Mock
    private SolarInfoRepository solarInfoRepository;
    @Mock
    private WebClient webClient;
    @Mock
    private WebClient.RequestHeadersUriSpec<?> requestHeadersUriSpec;
    @Mock
    private WebClient.RequestHeadersSpec<?> requestHeadersSpec;
    @Mock
    private WebClient.ResponseSpec responseSpec;
    @Mock
    private DateTimeFormatter dateFormatter;
    @InjectMocks
    private SolarWatchService solarWatchService;

    @BeforeEach
    void setUp() {
        solarWatchService = new SolarWatchService(webClient, dateFormatter, cityRepository, solarInfoRepository);
    }

    @Test
    void testGetSolarInfo_CityFoundInDatabase() {
        // Arrange
        String cityName = "london";//uses lowercase in method
        String country = "GB";
        double latitude = 51.5073219;
        double longitude = -0.1276474;
        String state = "England";
        String date = "2024-05-25";

        City city = new City();
        city.setName(cityName);
        city.setCountry(country);
        city.setLatitude(latitude);
        city.setLongitude(longitude);
        city.setState(state);

        SolarInfo solarInfo = new SolarInfo();
        solarInfo.setCity(city);
        solarInfo.setDate(date);
        solarInfo.setSunrise("3:52:45 AM");
        solarInfo.setSunset("8:02:16 PM");

        when(cityRepository.findByNameAndCountryAndStateIgnoreCase(cityName, country, state)).thenReturn(Set.of(city));
        when(solarInfoRepository.findByCityAndDate(city, date)).thenReturn(Optional.of(solarInfo));

        // Act
        Set<SolarInfoDTO> solarInfoDTOSet = solarWatchService.getSolarInfo(cityName, country, state, date);

        // Assert
        assertEquals(1, solarInfoDTOSet.size());
        SolarInfoDTO solarInfoDTO = solarInfoDTOSet.iterator().next();
        assertEquals(cityName, solarInfoDTO.name());
        assertEquals(country, solarInfoDTO.country());
        assertEquals(state, solarInfoDTO.state());
        assertEquals("3:52:45 AM", solarInfoDTO.sunrise());
        assertEquals("8:02:16 PM", solarInfoDTO.sunset());
    }

//    @Test
//    void testGetSolarInfo_CityNotFoundInDatabase() {
//        // Arrange
//        String cityName = "paris";
//        String country = "FR";
//        String state = "Ile-de-France";
//        String date = "2024-05-25";
//
//        when(cityRepository.findByNameAndCountryAndState(cityName, country, state)).thenReturn(new HashSet<>());
//
//        CityDTO cityDTO = new CityDTO(cityName, country, state, 48.8566, 2.3522);
//        City city = new City();
//        city.setName(cityName);
//        city.setCountry(country);
//        city.setState(state);
//        city.setLatitude(48.8566);
//        city.setLongitude(2.3522);
//        city.setPublicId(UUID.randomUUID());
//
//        SunriseSunsetDTO sunriseSunsetDTO = new SunriseSunsetDTO("3:54:55 AM", "7:40:31 PM");
//        SunriseSunsetResultsResponse response = new SunriseSunsetResultsResponse(sunriseSunsetDTO);
//
//        when(webClient.get()).thenReturn(requestHeadersUriSpec);
//        when(requestHeadersUriSpec.uri(anyString(), any(Object[].class))).thenReturn(requestHeadersSpec);
//        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
//        when(responseSpec.bodyToMono(CityDTO[].class)).thenReturn(Mono.just(new CityDTO[]{cityDTO}));
//        when(responseSpec.bodyToMono(SunriseSunsetResultsResponse.class)).thenReturn(Mono.just(response));
//
//        when(cityRepository.save(any(City.class))).thenReturn(city);
//        when(solarInfoRepository.save(any(SolarInfo.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//        // Act
//        Set<SolarInfoDTO> solarInfoDTOSet = solarWatchService.getSolarInfo(cityName, country, state, date);
//
//        // Assert
//        assertEquals(1, solarInfoDTOSet.size());
//        SolarInfoDTO solarInfoDTO = solarInfoDTOSet.iterator().next();
//        assertEquals(cityName, solarInfoDTO.name());
//        assertEquals(country, solarInfoDTO.country());
//        assertEquals(state, solarInfoDTO.state());
//        assertEquals("3:54:55 AM", solarInfoDTO.sunrise());
//        assertEquals("7:40:31 PM", solarInfoDTO.sunset());
//
//        verify(cityRepository).findByNameAndCountryAndState(cityName, country, state);
//        verify(cityRepository).save(any(City.class));
//        verify(solarInfoRepository).save(any(SolarInfo.class));
//    }
}