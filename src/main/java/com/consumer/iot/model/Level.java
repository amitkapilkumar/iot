package com.consumer.iot.model;

import com.consumer.iot.exception.InvalidEnumValueException;
import com.google.common.base.Strings;

public enum Level {
    FULL("Full"), HIGH("High"), MEDIUM("Medium"), LOW("Low"), CRITICAL("Critical");

    private String value;


    Level(String value) {
        this.value = value;
    }

    public static Level fromValue(String value) {
        if(Strings.isNullOrEmpty(value)) {
            throw new InvalidEnumValueException("Invalid enum value : " + value);
        }

        for(Level v : Level.values()) {
            if(value.equalsIgnoreCase(v.value)) {
                return v;
            }
        }
        throw new InvalidEnumValueException("Invalid enum value : " + value);
    }
}
