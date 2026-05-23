package com.elktini.iotfleet.exception;

public class DeviceStateConflictException extends RuntimeException {
    public DeviceStateConflictException(String message) {
        super(message);
    }
}
