package com.booking.system.exception;

public class BlockNotFoundException extends RuntimeException {

    public BlockNotFoundException(Long id) {
        super("Block with id " + id + " not found");
    }
}
