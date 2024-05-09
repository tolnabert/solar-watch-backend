package com.codecool.solarwatch.model.entity;

import jakarta.persistence.*;

import java.util.Objects;
import java.util.UUID;

@Entity
public class SolarInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private UUID publicId;
    private String date;
    private String sunrise;
    private String sunset;

    @ManyToOne(fetch = FetchType.LAZY)
    private City city;

    public SolarInfo() {
    }

    public UUID getPublicId() {
        return publicId;
    }

    public void setPublicId(UUID publicId) {
        this.publicId = publicId;
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
