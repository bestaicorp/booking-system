package com.booking.system.exception;

public class GuestNotFoundException extends RuntimeException {

    public GuestNotFoundException(Long id) {
        super("Guest with id " + id + " not found");
    }
}
