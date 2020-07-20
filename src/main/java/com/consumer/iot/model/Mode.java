package com.consumer.iot.model;

import com.consumer.iot.exception.InvalidEnumValueException;
import com.google.common.base.Strings;

public enum Mode {
    ON("On"), OFF("Off");

    private String value;

    Mode(String value) {
        this.value = value;
    }

    public static Mode fromValue(String value) {
        if(Strings.isNullOrEmpty(value)) {
            throw new InvalidEnumValueException("Invalid enum value : " + value);
        }

        for(Mode v : Mode.values()) {
            if(value.equalsIgnoreCase(v.value)) {
                return v;
            }
        }
        throw new InvalidEnumValueException("Invalid enum value : " + value);
    }
}
