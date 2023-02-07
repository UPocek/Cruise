package com.cruise.Cruise.driver.Services;

public class NoDriverAvailableForRideException extends Exception {
    public NoDriverAvailableForRideException(String errorMessage) {
        super(errorMessage);
    }
}
