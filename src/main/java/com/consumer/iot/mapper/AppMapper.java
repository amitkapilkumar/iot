package com.consumer.iot.mapper;

import com.consumer.iot.dto.response.DeviceResponse;
import com.consumer.iot.model.Device;

import java.text.ParseException;

public interface AppMapper {
    Device map(String row) throws ParseException;
    DeviceResponse map(Device device);
}
