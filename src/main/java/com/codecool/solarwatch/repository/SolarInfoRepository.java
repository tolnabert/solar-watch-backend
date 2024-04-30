package com.codecool.solarwatch.repository;

import com.codecool.solarwatch.model.city.City;
import com.codecool.solarwatch.model.solarinfo.SolarInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolarInfoRepository extends JpaRepository<SolarInfo, Long> {

    SolarInfo findByCityAndDate(City city, String date);
}
