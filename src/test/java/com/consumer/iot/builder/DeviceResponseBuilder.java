package com.consumer.iot.builder;

import com.consumer.iot.dto.response.DeviceResponse;

public class DeviceResponseBuilder {
    private String id;
    private String name;
    private String dateTime;
    private String longitude;
    private String latitude;
    private String status;
    private String battery;
    private String description;

    private DeviceResponseBuilder() {}

    public static DeviceResponseBuilder aDeviceResponseBuilder() {
        return new DeviceResponseBuilder();
    }

    public DeviceResponseBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public DeviceResponseBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public DeviceResponseBuilder withDateTime(String dateTime) {
        this.dateTime = dateTime;
        return this;
    }

    public DeviceResponseBuilder withLongitude(String longitude) {
        this.longitude = longitude;
        return this;
    }

    public DeviceResponseBuilder withLatitude(String latitude) {
        this.latitude = latitude;
        return this;
    }

    public DeviceResponseBuilder withStatus(String status) {
        this.status = status;
        return this;
    }

    public DeviceResponseBuilder withBattery(String battery) {
        this.battery = battery;
        return this;
    }

    public DeviceResponseBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public DeviceResponse build() {
        DeviceResponse response = new DeviceResponse();
        response.setId(id);
        response.setName(name);
        response.setDateTime(dateTime);
        response.setStatus(status);
        response.setDescription(description);
        response.setBattery(battery);
        response.setLatitude(latitude);
        response.setLongitude(longitude);
        return response;
    }
}
