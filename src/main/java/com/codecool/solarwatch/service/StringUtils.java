package com.codecool.solarwatch.service;

import java.util.Arrays;
import java.util.stream.Collectors;

public class StringUtils {
    public static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Arrays.stream(str.split(" "))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }

    public static String capitalizeCountryCode(String countryCode) {
        return countryCode != null ? countryCode.toUpperCase() : null;
    }
}