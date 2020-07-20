package com.consumer.iot.model;

import java.util.Date;

public class Device {
    private String id;
    private Mode airplaneMode;
    private Level batteryLevel;
    private Date dateTime;
    private String longitude;
    private String latitude;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public Mode getAirplaneMode() {
        return airplaneMode;
    }

    public void setAirplaneMode(Mode airplaneMode) {
        this.airplaneMode = airplaneMode;
    }

    public Level getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(Level batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

}
