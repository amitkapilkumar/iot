package com.consumer.iot.builder;

import com.consumer.iot.model.Device;
import com.consumer.iot.model.Level;
import com.consumer.iot.model.Mode;

import java.util.Date;

public class DeviceBuilder {
    private String id;
    private Mode airplaneMode;
    private Level batteryLevel;
    private Date dateTime;
    private String longitude;
    private String latitude;

    private DeviceBuilder() {}

    public static DeviceBuilder aDeviceBuilder() {
        return new DeviceBuilder();
    }

    public DeviceBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public DeviceBuilder withAirplaneMode(Mode mode) {
        this.airplaneMode = mode;
        return this;
    }

    public DeviceBuilder withLevel(Level level) {
        this.batteryLevel = level;
        return this;
    }

    public DeviceBuilder withDate(Date date) {
        this.dateTime = date;
        return this;
    }

    public DeviceBuilder withLongitude(String longitude) {
        this.longitude = longitude;
        return this;
    }

    public DeviceBuilder withLatitude(String latitude) {
        this.latitude = latitude;
        return this;
    }

    public Device build() {
        Device device = new Device();
        device.setId(id);
        device.setAirplaneMode(airplaneMode);
        device.setBatteryLevel(batteryLevel);
        device.setDateTime(dateTime);
        device.setLongitude(longitude);
        device.setLatitude(latitude);
        return device;
    }

}
