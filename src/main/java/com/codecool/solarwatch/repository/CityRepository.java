package com.codecool.solarwatch.repository;

import com.codecool.solarwatch.model.city.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {

    Set<City> findByName(String cityName);

    City findByNameAndLatAndLon(String cityName, double latitude, double longitude);
}
