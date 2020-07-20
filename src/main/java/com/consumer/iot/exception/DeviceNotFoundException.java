package com.consumer.iot.exception;

public class DeviceNotFoundException extends RuntimeException {
    public DeviceNotFoundException(String msg) {
        super(msg);
    }
}
