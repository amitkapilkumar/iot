package com.consumer.iot.exception;

public class DeviceNotLocatedException extends RuntimeException {
    public DeviceNotLocatedException(String msg) {
        super(msg);
    }
}
