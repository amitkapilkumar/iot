package com.consumer.iot.mapper;

import com.consumer.iot.dto.response.DeviceResponse;
import com.consumer.iot.model.Device;
import com.consumer.iot.model.Level;
import com.consumer.iot.model.Mode;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.text.ParseException;

import static com.consumer.iot.model.Mode.ON;
import static com.consumer.iot.util.AppUtil.ACTIVE;
import static com.consumer.iot.util.AppUtil.DESCRIPTION_LOCATION_IDENTIFIED;
import static com.consumer.iot.util.AppUtil.DESCRIPTION_LOCATION_UNIDENTIFIED;
import static com.consumer.iot.util.AppUtil.EMPTY;
import static com.consumer.iot.util.AppUtil.IN_ACTIVE;
import static com.consumer.iot.util.AppUtil.SPLIT_TOKEN;
import static com.consumer.iot.util.AppUtil.formatDate;
import static com.consumer.iot.util.AppUtil.getDate;
import static com.consumer.iot.util.AppUtil.getName;

@Component
public class AppMapperImpl implements AppMapper {

    @Override
    public Device map(String row) throws ParseException {
        String[] tokens = row.split(SPLIT_TOKEN);
        Device device = new Device();
        device.setId(tokens[0].trim());
        device.setAirplaneMode(Mode.fromValue(tokens[1].trim()));
        device.setBatteryLevel(getLevel(tokens[2].trim()));
        device.setDateTime(getDate(tokens[3].trim()));
        if(tokens.length > 4) {
            device.setLongitude(tokens[4].trim());
            device.setLatitude(tokens[5].trim());
        }
        return device;
    }

    @Override
    public DeviceResponse map(Device device) {
        DeviceResponse response = new DeviceResponse();
        response.setId(device.getId());
        response.setName(getName(device.getId()));
        response.setDateTime(formatDate(device.getDateTime()));
        response.setLongitude(device.getAirplaneMode() == ON? EMPTY : device.getLongitude());
        response.setLatitude(device.getAirplaneMode() == ON? EMPTY : device.getLatitude());
        response.setStatus(getStatus(device));
        response.setBattery(device.getBatteryLevel().toString());
        response.setDescription(getDescription(device));
        return response;
    }

    private String getStatus(Device device) {
        switch (device.getAirplaneMode()) {
            case ON:
                return IN_ACTIVE;
            case OFF:
                if(StringUtils.isEmpty(device.getLongitude()) || StringUtils.isEmpty(device.getLatitude())) {
                    return IN_ACTIVE;
                }
        }
        return ACTIVE;
    }

    private String getDescription(Device device) {
        if(getStatus(device).equals(ACTIVE)) {
            return DESCRIPTION_LOCATION_IDENTIFIED;
        }
        return DESCRIPTION_LOCATION_UNIDENTIFIED;
    }

    private Level getLevel(String level) {
        int iLevel = Integer.parseInt(level);

        if(iLevel >= 98) {
            return Level.FULL;
        }

        if(iLevel >= 60) {
            return Level.HIGH;
        }

        if(iLevel >= 40) {
            return Level.MEDIUM;
        }

        if(iLevel >= 10) {
            return Level.LOW;
        }

        return Level.CRITICAL;
    }
}
