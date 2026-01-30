package com.booking.system.exception;

public class PropertyNotFoundException extends RuntimeException {

    public PropertyNotFoundException(Long id) {
        super("Property with id " + id + " not found");
    }
}
