package com.codecool.solarwatch.model.entity;

import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private UUID publicId;
    private String name;
    private String country;//other table with relation
    private String state;
    private double lon;
    private double lat;
    @OneToMany(mappedBy = "city")
    private List<SolarInfo> forecasts;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public UUID getPublicId() {
        return publicId;
    }

    public void setPublicId(UUID publicId) {
        this.publicId = publicId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public List<SolarInfo> getForecasts() {
        return forecasts;
    }

    public void setForecasts(List<SolarInfo> forecasts) {
        this.forecasts = forecasts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        City city = (City) o;
        return Double.compare(lon, city.lon) == 0 && Double.compare(lat, city.lat) == 0 && Objects.equals(name, city.name) && Objects.equals(country, city.country) && Objects.equals(state, city.state) && Objects.equals(forecasts, city.forecasts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, country, state, lon, lat, forecasts);
    }

    @Override
    public String toString() {
        return "City{" +
                "publicId=" + publicId +
                ", name='" + name + '\'' +
                ", country='" + country + '\'' +
                ", state='" + state + '\'' +
                ", lon=" + lon +
                ", lat=" + lat +
                '}';
    }
}
