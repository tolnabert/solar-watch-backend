package com.codecool.solarwatch.service;

public interface UrlQueryValidator {
    void validateDate(String dateStr);

    void validateLimit(int limit);
}