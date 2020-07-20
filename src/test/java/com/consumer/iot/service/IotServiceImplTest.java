package com.consumer.iot.service;

import com.consumer.iot.builder.DeviceBuilder;
import com.consumer.iot.dto.response.DeviceResponse;
import com.consumer.iot.exception.DeviceNotFoundException;
import com.consumer.iot.exception.DeviceNotLocatedException;
import com.consumer.iot.mapper.AppMapper;
import com.consumer.iot.mapper.AppMapperImpl;
import com.consumer.iot.model.Device;
import com.consumer.iot.model.Level;
import com.consumer.iot.model.Mode;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.consumer.iot.util.AppUtil.CYCLE_PLUS_TRACKER;
import static com.consumer.iot.util.AppUtil.DESCRIPTION_LOCATION_IDENTIFIED;
import static com.consumer.iot.util.AppUtil.IN_ACTIVE;
import static com.consumer.iot.util.AppUtil.getDate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class IotServiceImplTest {
    @InjectMocks
    private IotServiceImpl iotServiceImpl;

    @Spy
    private AppMapper appMapper = new AppMapperImpl();

    @After
    public void verifyAfter() {
        verifyNoMoreInteractions(appMapper);
    }

    @Test
    public void testLoadDataWithNotExistingFile() throws IOException, ParseException {
        String filepath = "C:\\Users\\amitk\\MyStuffs\\data1.csv";

        assertFalse(iotServiceImpl.loadData(filepath));
    }

    @Test
    public void testLoadData() throws IOException, ParseException {
        String filepath = "C:\\Users\\amitk\\MyStuffs\\data.csv";

        boolean status = iotServiceImpl.loadData(filepath);

        if(status) {
            Map<String, List<Device>> devices = (Map<String, List<Device>>) ReflectionTestUtils.getField(iotServiceImpl, "devices");
            assertNotNull(devices);
            assertEquals(devices.size(), 3);

            verify(appMapper, times(9)).map(anyString());
        }
    }

    @Test
    public void testGetDeviceWithTimestampBeforeFirstDeviceTimeStamp() throws ParseException {
        List<Device> list = new ArrayList<>();
        list.add(DeviceBuilder.aDeviceBuilder().withId("WG11155631").withAirplaneMode(Mode.OFF).withLevel(Level.HIGH)
                .withDate(getDate("27/02/2020 04:31:17")).withLatitude("-0.1736").withLongitude("51.5185")
                .build());

        list.add(DeviceBuilder.aDeviceBuilder().withId("WG11155631").withAirplaneMode(Mode.OFF).withLevel(Level.HIGH)
                .withDate(getDate("28/02/2020 10:31:17")).withLatitude("-0.1778").withLongitude("51.5131")
                .build());

        Map<String, List<Device>> devices = new HashMap<>();
        devices.put("WG11155631", list);

        ReflectionTestUtils.setField(iotServiceImpl, "devices", devices);
        DeviceResponse actualResponse = iotServiceImpl.getDevice("WG11155631", "1582605137000");

        assertEquals(actualResponse.getId(), list.get(0).getId());
        assertEquals(actualResponse.getName(), CYCLE_PLUS_TRACKER);
        assertEquals(actualResponse.getBattery(), "HIGH");
        assertEquals(actualResponse.getDescription(), DESCRIPTION_LOCATION_IDENTIFIED);

        verify(appMapper).map(any(Device.class));
    }

    @Test(expected = DeviceNotLocatedException.class)
    public void testGetDeviceWithTimestampBeforeFirstDeviceTimeStampWithException() throws ParseException {
        List<Device> list = new ArrayList<>();
        list.add(DeviceBuilder.aDeviceBuilder().withId("WG11155631").withAirplaneMode(Mode.OFF).withLevel(Level.HIGH)
                .withDate(getDate("27/02/2020 04:33:17")).withLatitude(null).withLongitude("51.5185")
                .build());

        list.add(DeviceBuilder.aDeviceBuilder().withId("WG11155631").withAirplaneMode(Mode.OFF).withLevel(Level.HIGH)
                .withDate(getDate("28/02/2020 10:31:17")).withLatitude("-0.1778").withLongitude("51.5131")
                .build());

        Map<String, List<Device>> devices = new HashMap<>();
        devices.put("WG11155631", list);

        ReflectionTestUtils.setField(iotServiceImpl, "devices", devices);
        iotServiceImpl.getDevice("WG11155631", "1582605137000");
    }

    @Test
    public void testGetDeviceWithSingleDeviceEntry() throws ParseException {
        List<Device> list = new ArrayList<>();
        list.add(DeviceBuilder.aDeviceBuilder().withId("WG11155631").withAirplaneMode(Mode.OFF).withLevel(Level.HIGH)
                .withDate(getDate("27/02/2020 04:31:17")).withLatitude("-0.1736").withLongitude("51.5185")
                .build());


        Map<String, List<Device>> devices = new HashMap<>();
        devices.put("WG11155631", list);

        ReflectionTestUtils.setField(iotServiceImpl, "devices", devices);
        DeviceResponse actualResponse = iotServiceImpl.getDevice("WG11155631", "1582605137000");

        assertEquals(actualResponse.getId(), list.get(0).getId());
        assertEquals(actualResponse.getName(), CYCLE_PLUS_TRACKER);
        assertEquals(actualResponse.getBattery(), "HIGH");
        assertEquals(actualResponse.getDescription(), DESCRIPTION_LOCATION_IDENTIFIED);

        verify(appMapper).map(any(Device.class));
    }

    @Test(expected = DeviceNotLocatedException.class)
    public void testGetDeviceWithGPSDataOff() throws ParseException {
        List<Device> list = new ArrayList<>();
        list.add(DeviceBuilder.aDeviceBuilder().withId("WG11155631").withAirplaneMode(Mode.OFF).withLevel(Level.HIGH)
                .withDate(getDate("27/02/2020 04:31:17")).withLatitude(null).withLongitude(null)
                .build());


        Map<String, List<Device>> devices = new HashMap<>();
        devices.put("WG11155631", list);

        ReflectionTestUtils.setField(iotServiceImpl, "devices", devices);
        iotServiceImpl.getDevice("WG11155631", "1582605137000");

    }

    @Test(expected = DeviceNotFoundException.class)
    public void testGetDeviceWithEmpptyMap() {
        iotServiceImpl.getDevice("WG11155631", null);
    }

    @Test
    public void testGetDeviceWithoutTimestamp() throws ParseException {
        List<Device> list = new ArrayList<>();
        list.add(DeviceBuilder.aDeviceBuilder().withId("WG11155631").withAirplaneMode(Mode.OFF).withLevel(Level.HIGH)
                .withDate(getDate("27/02/2020 04:31:17")).withLatitude("-0.1778").withLongitude("51.5131")
                .build());

        list.add(DeviceBuilder.aDeviceBuilder().withId("WG11155631").withAirplaneMode(Mode.OFF).withLevel(Level.HIGH)
                .withDate(getDate("28/02/2020 10:31:17")).withLatitude("-0.1778").withLongitude("51.5131")
                .build());

        list.add(DeviceBuilder.aDeviceBuilder().withId("WG11155631").withAirplaneMode(Mode.OFF).withLevel(Level.HIGH)
                .withDate(getDate("28/02/2020 10:31:17")).withLatitude("-0.1778").withLongitude("51.5131")
                .build());

        Map<String, List<Device>> devices = new HashMap<>();
        devices.put("WG11155631", list);

        ReflectionTestUtils.setField(iotServiceImpl, "devices", devices);
        DeviceResponse actualResponse = iotServiceImpl.getDevice("WG11155631", null);

        assertEquals(actualResponse.getId(), list.get(0).getId());
        assertEquals(actualResponse.getName(), CYCLE_PLUS_TRACKER);
        assertEquals(actualResponse.getBattery(), "HIGH");
        assertEquals(actualResponse.getDescription(), DESCRIPTION_LOCATION_IDENTIFIED);
        assertEquals(actualResponse.getStatus(), IN_ACTIVE);

        verify(appMapper).map(any(Device.class));
    }
}
