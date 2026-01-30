package com.booking.system.exception;

public class DateAlreadyBookedException extends RuntimeException {

    public DateAlreadyBookedException(String message) {
        super(message);
    }
}
