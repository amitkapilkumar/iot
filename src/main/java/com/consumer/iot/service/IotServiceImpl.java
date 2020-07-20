package com.consumer.iot.service;

import com.consumer.iot.dto.response.DeviceResponse;
import com.consumer.iot.exception.DeviceNotFoundException;
import com.consumer.iot.exception.DeviceNotLocatedException;
import com.consumer.iot.mapper.AppMapper;
import com.consumer.iot.model.Device;
import com.consumer.iot.model.Mode;
import com.consumer.iot.util.AppUtil;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

import static com.consumer.iot.util.AppUtil.CYCLE_PLUS_TRACKER;
import static com.consumer.iot.util.AppUtil.IN_ACTIVE;
import static com.consumer.iot.util.AppUtil.formatDate;

@Service
public class IotServiceImpl implements IotService {

    @Autowired
    private AppMapper appMapper;

    private Map<String, List<Device>> devices;

    @Override
    public boolean loadData(String filepath) throws IOException, ParseException {
        if(!AppUtil.isFileExists(filepath)) {
            return false;
        }

        try (FileInputStream fis = new FileInputStream(filepath); Scanner scanner = new Scanner(fis)) {
            init(); //re-initialise Map of devices
            processIfContainsHeaderRow(scanner);
            while (scanner.hasNextLine()) {
                Device device = appMapper.map(scanner.nextLine());
                if (devices.containsKey(device.getId())) {
                    devices.get(device.getId()).add(device);
                } else {
                    List<Device> list = new ArrayList<>();
                    list.add(device);
                    devices.put(device.getId(), list);
                }
            }
        }

        System.out.println("Data loaded from file..");
        return true;
    }

    @Override
    public DeviceResponse getDevice(String id, String timestamp) {
        if(Strings.isNullOrEmpty(id) || Objects.isNull(devices) || devices.isEmpty() || !devices.containsKey(id)) {
            throw new DeviceNotFoundException("Device not found");
        }
        List<Device> deviceList = devices.get(id);
        if(deviceList.size() == 1) {
            if(isGPSDataOff(deviceList.get(0))) {
                throw new DeviceNotLocatedException("Device GPS data not available");
            }
            return appMapper.map(deviceList.get(0));
        }

        deviceList.sort(Comparator.comparing(Device::getDateTime));

        for(Device device : deviceList) {
            System.out.println(device.getId() + " : " + device.getLongitude() + " : " + formatDate(device.getDateTime()));
        }

        if(Strings.isNullOrEmpty(timestamp)) {
            System.out.println("TimeStamp is empty/null");
            return searchClosestDevice(getCurrentDate(), deviceList);
        }
        else {
            System.out.println("TimeStamp is : " + timestamp);
            return searchClosestDevice(new Date(Long.parseLong(timestamp)), deviceList);
        }
    }

    private Date getCurrentDate() {
        return Date.from(LocalDateTime.now().atZone(ZoneId.of("UTC")).toInstant());
    }

    private DeviceResponse searchClosestDevice(Date date, List<Device> devices) {
        System.out.println(formatDate(date));

        if(date.before(devices.get(0).getDateTime())) {
            if(isGPSDataOff(devices.get(0))) {
                throw new DeviceNotLocatedException("Device GPS data not available");
            }
            return appMapper.map(devices.get(0));
        }

        long minDiff = getDateDifferences(date, devices.get(0).getDateTime());
        Device deviceToBeReturned = devices.get(0);
        System.out.println(minDiff + " : " + formatDate(devices.get(0).getDateTime()));

        for(int i=1; i < devices.size(); i++) {
            long diff = getDateDifferences(date, devices.get(i).getDateTime());
            System.out.println(diff + " : " + formatDate(devices.get(i).getDateTime()));
            if(minDiff > diff) {
                deviceToBeReturned = devices.get(i);
                minDiff = diff;
            }
        }
        System.out.println("Returning : " + formatDate(deviceToBeReturned.getDateTime()));
        if(isGPSDataOff(deviceToBeReturned)) {
            throw new DeviceNotLocatedException("Device GPS data not available");
        }
        return detectDynamicActivityTracking(appMapper.map(deviceToBeReturned), devices);
    }

    private DeviceResponse detectDynamicActivityTracking(DeviceResponse response, List<Device> devices) {
        if(response.getName().equals(CYCLE_PLUS_TRACKER) && devices.size() >= 3) { // check for 2 consecutive readings of lat/lon
            for(int i=2; i < devices.size(); i++) {
                Device first = devices.get(i-2);
                Device second = devices.get(i-1);
                Device third = devices.get(i);

                if(first.getLongitude().equals(second.getLongitude()) && second.getLongitude().equals(third.getLongitude())) {
                    if(first.getLatitude().equals(second.getLatitude()) && second.getLatitude().equals(third.getLatitude())) {
                        System.out.println("Current Status : " + response.getStatus());
                        response.setStatus(IN_ACTIVE);
                        return response;
                    }
                }
            }
        }
        return response;
    }

    private boolean isGPSDataOff(Device device) {
        if(device.getAirplaneMode() == Mode.OFF &&
                (Strings.isNullOrEmpty(device.getLongitude()) || Strings.isNullOrEmpty(device.getLatitude()))) {
            return true;
        }
        return false;
    }

    private long getDateDifferences(Date d1, Date d2) {
        return (Math.abs(d1.getTime() - d2.getTime())) / 1000; // returns difference in seconds
    }

    private void processIfContainsHeaderRow(Scanner scanner) throws ParseException {
        if(scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if(line.startsWith("id")) {
                return;
            }
            Device device = appMapper.map(line);
            List<Device> list = new ArrayList<>();
            list.add(device);
            devices.put(device.getId(), list);
        }
    }

    private void init() {
        devices = new ConcurrentHashMap<>();
    }
}
