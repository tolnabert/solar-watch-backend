package com.codecool.solarwatch.repository;

import com.codecool.solarwatch.model.entity.City;
import com.codecool.solarwatch.model.entity.SolarInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SolarInfoRepository extends JpaRepository<SolarInfo, Long> {

    Optional<SolarInfo> findByCityAndDate(City city, String date);
}
