package com.codecool.solarwatch.model.solarinfo;

import com.codecool.solarwatch.model.city.City;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class SolarInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String date;
    private String sunrise;
    private String sunset;

    @ManyToOne(fetch = FetchType.LAZY)
    private City city;

    public SolarInfo() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSunrise() {
        return sunrise;
    }

    public void setSunrise(String sunrise) {
        this.sunrise = sunrise;
    }

    public String getSunset() {
        return sunset;
    }

    public void setSunset(String sunset) {
        this.sunset = sunset;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SolarInfo solarInfo = (SolarInfo) o;
        return Objects.equals(date, solarInfo.date) && Objects.equals(sunrise, solarInfo.sunrise) && Objects.equals(sunset, solarInfo.sunset) && Objects.equals(city, solarInfo.city);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, sunrise, sunset, city);
    }

    @Override
    public String toString() {
        return "SolarInfo{" +
                ", date='" + date + '\'' +
                ", sunrise='" + sunrise + '\'' +
                ", sunset='" + sunset + '\'' +
                '}';
    }
}
