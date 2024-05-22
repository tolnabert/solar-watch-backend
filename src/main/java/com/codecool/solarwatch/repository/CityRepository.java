package com.codecool.solarwatch.repository;

import com.codecool.solarwatch.model.entity.City;
import com.codecool.solarwatch.model.entity.SolarInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {

    Set<City> findByNameAndCountry(String cityName, String country);
    Set<City> findByNameAndCountryAndState(String cityName, String country, String state);
}
