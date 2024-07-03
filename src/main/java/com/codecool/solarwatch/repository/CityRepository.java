package com.codecool.solarwatch.repository;

import com.codecool.solarwatch.model.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {

    Set<City> findByNameAndCountryIgnoreCase(String cityName, String country);

    Set<City> findByNameAndCountryAndStateIgnoreCase(String cityName, String country, String state);
    Optional<City> findByNameAndCountryAndState(String cityName, String country, String state);
}
