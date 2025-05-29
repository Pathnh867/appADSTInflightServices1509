// src/main/java/com/example/vietflightinventory/utils/ValidationUtils.java
package com.example.vietflightinventory.utils;

import java.util.regex.Pattern;

public class ValidationUtils {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^(\\+84|0)[0-9]{9,10}$");

    private static final Pattern FLIGHT_NUMBER_PATTERN =
            Pattern.compile("^[A-Z]{2}[0-9]{1,4}$");

    private static final Pattern AIRCRAFT_NUMBER_PATTERN =
            Pattern.compile("^[A-Z]{2}-[A-Z0-9]{4,6}$");

    private static final Pattern AIRPORT_CODE_PATTERN =
            Pattern.compile("^[A-Z]{3}$");

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPhoneNumber(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    public static boolean isValidFlightNumber(String flightNumber) {
        return flightNumber != null && FLIGHT_NUMBER_PATTERN.matcher(flightNumber).matches();
    }

    public static boolean isValidAircraftNumber(String aircraftNumber) {
        return aircraftNumber != null && AIRCRAFT_NUMBER_PATTERN.matcher(aircraftNumber).matches();
    }

    public static boolean isValidAirportCode(String airportCode) {
        return airportCode != null && AIRPORT_CODE_PATTERN.matcher(airportCode).matches();
    }

    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }

    public static boolean isValidLength(String str, int minLength, int maxLength) {
        if (str == null) return false;
        int length = str.trim().length();
        return length >= minLength && length <= maxLength;
    }
}