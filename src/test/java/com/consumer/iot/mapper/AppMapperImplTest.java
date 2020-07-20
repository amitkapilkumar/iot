package com.consumer.iot.mapper;

import com.consumer.iot.builder.DeviceBuilder;
import com.consumer.iot.dto.response.DeviceResponse;
import com.consumer.iot.model.Device;
import com.consumer.iot.model.Level;
import com.consumer.iot.model.Mode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.text.ParseException;
import java.util.Date;

import static com.consumer.iot.util.AppUtil.ACTIVE;
import static com.consumer.iot.util.AppUtil.CYCLE_PLUS_TRACKER;
import static com.consumer.iot.util.AppUtil.DESCRIPTION_LOCATION_IDENTIFIED;
import static com.consumer.iot.util.AppUtil.DESCRIPTION_LOCATION_UNIDENTIFIED;
import static com.consumer.iot.util.AppUtil.GENERAL_TRACKER;
import static com.consumer.iot.util.AppUtil.IN_ACTIVE;
import static com.consumer.iot.util.AppUtil.formatDate;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class AppMapperImplTest {
    @InjectMocks
    private AppMapperImpl appMapperImpl;

    @Test
    public void testMapToDevice() throws ParseException {
        Device device = appMapperImpl.map("WG11155631,off,23,28/02/2020 04:31:17,51.5185,-0.1736");
        assertEquals(device.getId(), "WG11155631");
        assertEquals(device.getAirplaneMode(), Mode.OFF);
        assertEquals(device.getBatteryLevel(), Level.LOW);
        assertEquals(formatDate(device.getDateTime()), "28/02/2020 04:31:17");
        assertEquals(device.getLongitude(), "51.5185");
        assertEquals(device.getLatitude(), "-0.1736");

        device = appMapperImpl.map("6911155631,on,98,28/02/2020 04:31:17,51.5185,-0.1736");
        assertEquals(device.getAirplaneMode(), Mode.ON);
        assertEquals(device.getBatteryLevel(), Level.FULL);

        device = appMapperImpl.map("6911155631,on,63,28/02/2020 04:31:17,51.5185,-0.1736");
        assertEquals(device.getBatteryLevel(), Level.HIGH);

        device = appMapperImpl.map("6911155631,on,41,28/02/2020 04:31:17,51.5185,-0.1736");
        assertEquals(device.getBatteryLevel(), Level.MEDIUM);

        device = appMapperImpl.map("6911155631,on,9,28/02/2020 04:31:17,51.5185,-0.1736");
        assertEquals(device.getBatteryLevel(), Level.CRITICAL);
    }

    @Test(expected = ParseException.class)
    public void testMapToDeviceWithParseException() throws ParseException {
        appMapperImpl.map("WG11155631,off,23,28-02-2020 04:31:17,51.5185,-0.1736");
    }

    @Test
    public void testMapToDeviceResponse() {
        Device device = DeviceBuilder.aDeviceBuilder()
                .withId("WG11155631").withAirplaneMode(Mode.OFF).withLevel(Level.HIGH)
                .withDate(new Date()).withLatitude("-0.1736").withLongitude("51.5185")
                .build();

        DeviceResponse response = appMapperImpl.map(device);

        assertEquals(response.getId(), device.getId());
        assertEquals(response.getLongitude(), device.getLongitude());
        assertEquals(response.getLatitude(), device.getLatitude());
        assertEquals(response.getName(), CYCLE_PLUS_TRACKER);
        assertEquals(response.getBattery(), Level.HIGH.toString());
        assertEquals(response.getStatus(), ACTIVE);
        assertEquals(response.getDescription(), DESCRIPTION_LOCATION_IDENTIFIED);

        device = DeviceBuilder.aDeviceBuilder()
                .withId("6911155631").withAirplaneMode(Mode.ON).withLevel(Level.MEDIUM)
                .withDate(new Date()).withLatitude("-0.1736").withLongitude("51.5185")
                .build();
        response = appMapperImpl.map(device);

        assertEquals(response.getName(), GENERAL_TRACKER);
        assertEquals(response.getBattery(), Level.MEDIUM.toString());
        assertEquals(response.getStatus(), IN_ACTIVE);
        assertEquals(response.getDescription(), DESCRIPTION_LOCATION_UNIDENTIFIED);

        device = DeviceBuilder.aDeviceBuilder()
                .withId("6911155631").withAirplaneMode(Mode.OFF).withLevel(Level.MEDIUM)
                .withDate(new Date()).withLatitude(null).withLongitude("51.5185")
                .build();
        response = appMapperImpl.map(device);

        assertEquals(response.getName(), GENERAL_TRACKER);
        assertEquals(response.getBattery(), Level.MEDIUM.toString());
        assertEquals(response.getStatus(), IN_ACTIVE);
        assertEquals(response.getDescription(), DESCRIPTION_LOCATION_UNIDENTIFIED);
    }
}
