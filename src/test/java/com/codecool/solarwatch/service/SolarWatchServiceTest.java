package com.codecool.solarwatch.service;

import com.codecool.solarwatch.model.dto.*;
import com.codecool.solarwatch.model.entity.City;
import com.codecool.solarwatch.model.entity.SolarInfo;
import com.codecool.solarwatch.repository.CityRepository;
import com.codecool.solarwatch.repository.SolarInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class SolarWatchServiceTest {

    @Mock
    private CityRepository cityRepository;

    @Mock
    private SolarInfoRepository solarInfoRepository;

    @InjectMocks
    private SolarWatchService solarWatchService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetSolarInfo_ExistingCity() {
        // Arrange
        String cityName = "Budapest";
        String country = "HU";
        String state = null;
        String date = "2024-07-30";

        City city = new City();
        city.setPublicId(UUID.randomUUID());
        city.setName(cityName.toLowerCase());
        city.setCountry(country.toLowerCase());
        city.setState(state);
        city.setLatitude(47.4979);
        city.setLongitude(19.0402);

        SolarInfo solarInfo = new SolarInfo();
        solarInfo.setCity(city);
        solarInfo.setDate(date);
        solarInfo.setSunrise("06:00:00");
        solarInfo.setSunset("20:00:00");

        when(cityRepository.findByNameAndCountryIgnoreCase(eq(cityName.toLowerCase()), eq(country.toLowerCase())))
                .thenReturn(Set.of(city));
        when(solarInfoRepository.findByCityAndDate(eq(city), eq(date)))
                .thenReturn(Optional.of(solarInfo));

        // Act
        Set<SolarInfoDTO> result = solarWatchService.getSolarInfo(cityName, country, state, date);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("06:00:00", result.iterator().next().sunrise());
    }

    @Test
    void testAddSolarInfo() {
        // Arrange
        AddSolarInfoDTO addSolarInfoDTO = new AddSolarInfoDTO("Budapest", "HU", null, "2024-07-30", 47.4979, 19.0402, "06:00:00", "20:00:00");

        City city = new City();
        city.setPublicId(UUID.randomUUID());
        city.setName(addSolarInfoDTO.cityName().toLowerCase());
        city.setCountry(addSolarInfoDTO.country().toLowerCase());
        city.setState(addSolarInfoDTO.state() != null ? addSolarInfoDTO.state().toLowerCase() : null);
        city.setLatitude(addSolarInfoDTO.latitude());
        city.setLongitude(addSolarInfoDTO.longitude());

        SolarInfo solarInfo = new SolarInfo();
        solarInfo.setPublicId(UUID.randomUUID());
        solarInfo.setCity(city);
        solarInfo.setDate(addSolarInfoDTO.date());
        solarInfo.setSunrise(addSolarInfoDTO.sunrise());
        solarInfo.setSunset(addSolarInfoDTO.sunset());

        when(cityRepository.findByNameAndCountryAndState(
                addSolarInfoDTO.cityName().toLowerCase(),
                addSolarInfoDTO.country().toLowerCase(),
                addSolarInfoDTO.state() != null ? addSolarInfoDTO.state().toLowerCase() : null))
                .thenReturn(Optional.empty());
        when(cityRepository.save(any(City.class))).thenReturn(city);
        when(solarInfoRepository.save(any(SolarInfo.class))).thenReturn(solarInfo);

        // Act
        solarWatchService.addSolarInfo(addSolarInfoDTO);

        // Assert
        verify(cityRepository).save(any(City.class));
        verify(solarInfoRepository).save(any(SolarInfo.class));
    }
}
