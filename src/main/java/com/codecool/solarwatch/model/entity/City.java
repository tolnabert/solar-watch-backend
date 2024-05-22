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
    private String country;
    private String state;
    private double longitude;
    private double latitude;
    @OneToMany(mappedBy = "city")
    private List<SolarInfo> solarInfos;

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

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public List<SolarInfo> getSolarInfos() {
        return solarInfos;
    }

    public void setSolarInfos(List<SolarInfo> solarInfos) {
        this.solarInfos = solarInfos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        City city = (City) o;
        return Double.compare(longitude, city.longitude) == 0 && Double.compare(latitude, city.latitude) == 0 && Objects.equals(name, city.name) && Objects.equals(country, city.country) && Objects.equals(state, city.state) && Objects.equals(solarInfos, city.solarInfos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, country, state, longitude, latitude, solarInfos);
    }

    @Override
    public String toString() {
        return "City{" +
                "publicId=" + publicId +
                ", name='" + name + '\'' +
                ", country='" + country + '\'' +
                ", state='" + state + '\'' +
                ", lon=" + longitude +
                ", lat=" + latitude +
                '}';
    }
}
