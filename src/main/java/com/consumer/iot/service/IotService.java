package com.consumer.iot.service;

import com.consumer.iot.dto.response.DeviceResponse;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;

public interface IotService {
    boolean loadData(String filepath) throws IOException, ParseException;
    DeviceResponse getDevice(String id, String timestamp);
}
