package com.consumer.iot.controller;

import com.consumer.iot.builder.DeviceResponseBuilder;
import com.consumer.iot.dto.request.FilePathRequest;
import com.consumer.iot.dto.response.DeviceResponse;
import com.consumer.iot.dto.response.ResponseObject;
import com.consumer.iot.exception.DeviceNotFoundException;
import com.consumer.iot.exception.DeviceNotLocatedException;
import com.consumer.iot.service.IotService;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.text.ParseException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AppControllerTest {
    @InjectMocks
    private AppController appController;

    @Mock
    private IotService iotService;

    @After
    public void verifyAfter() {
        verifyNoMoreInteractions(iotService);
    }

    @Test
    public void testLoadDataFromFile() throws IOException, ParseException {
        FilePathRequest request = new FilePathRequest();
        request.setFilepath("path/to/data.csv");

        when(iotService.loadData(request.getFilepath())).thenReturn(true);

        ResponseEntity<ResponseObject> response = appController.loadDataFromFile(request);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody().getDescription(), "Data refreshed ..");

        verify(iotService).loadData(request.getFilepath());
    }

    @Test
    public void testLoadDataFromFileWithFileNotExists() throws IOException, ParseException {
        FilePathRequest request = new FilePathRequest();
        request.setFilepath("path/to/data.csv");

        when(iotService.loadData(request.getFilepath())).thenReturn(false);

        ResponseEntity<ResponseObject> response = appController.loadDataFromFile(request);

        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertEquals(response.getBody().getDescription(), "ERROR: no data file found");

        verify(iotService).loadData(request.getFilepath());
    }

    @Test
    public void testLoadDataFromFileWithIOException() throws IOException, ParseException {
        FilePathRequest request = new FilePathRequest();
        request.setFilepath("path/to/data.csv");

        when(iotService.loadData(request.getFilepath())).thenThrow(new IOException("IO Exception"));

        ResponseEntity<ResponseObject> response = appController.loadDataFromFile(request);

        assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        assertEquals(response.getBody().getDescription(), "ERROR: A technical exception occurred IO Exception");

        verify(iotService).loadData(request.getFilepath());
    }

    @Test
    public void testGetDevice() {
        String productId = "WG11155631";
        String timestamp = "1582605137000";
        DeviceResponse response = DeviceResponseBuilder.aDeviceResponseBuilder()
                .withId("WG11155631").withName("CyclePlusTracker").withDateTime("28/02/2020 04:31:17")
                .withBattery("High").withDescription("SUCCESS: Location Identified").withStatus("Active")
                .withLongitude("51.5185").withLatitude("-0.1736")
                .build();

        when(iotService.getDevice(productId, timestamp)).thenReturn(response);

        ResponseEntity<DeviceResponse> actualResponse = (ResponseEntity<DeviceResponse>) appController.getDevice(productId, timestamp);
        assertEquals(actualResponse.getStatusCode(), HttpStatus.OK);
        assertEquals(actualResponse.getBody().getId(), response.getId());
        assertEquals(actualResponse.getBody().getName(), response.getName());
        assertEquals(actualResponse.getBody().getDateTime(), response.getDateTime());
        assertEquals(actualResponse.getBody().getBattery(), response.getBattery());
        assertEquals(actualResponse.getBody().getDescription(), response.getDescription());
        assertEquals(actualResponse.getBody().getStatus(), response.getStatus());
        assertEquals(actualResponse.getBody().getLongitude(), response.getLongitude());
        assertEquals(actualResponse.getBody().getLatitude(), response.getLatitude());

        verify(iotService).getDevice(productId, timestamp);
    }

    @Test
    public void testGetDeviceWithDeviceNotFoundException() {
        String productId = "WG11155631";
        String timestamp = "1582605137000";

        when(iotService.getDevice(productId, timestamp)).thenThrow(new DeviceNotFoundException("device not found"));

        ResponseEntity<ResponseObject> actualResponse = (ResponseEntity<ResponseObject>) appController.getDevice(productId, timestamp);
        assertEquals(actualResponse.getStatusCode(), HttpStatus.NOT_FOUND);

        verify(iotService).getDevice(productId, timestamp);
    }

    @Test
    public void testGetDeviceWithDeviceNotLocatedException() {
        String productId = "WG11155631";
        String timestamp = "1582605137000";

        when(iotService.getDevice(productId, timestamp)).thenThrow(new DeviceNotLocatedException("device not found"));

        ResponseEntity<ResponseObject> actualResponse = (ResponseEntity<ResponseObject>) appController.getDevice(productId, timestamp);
        assertEquals(actualResponse.getStatusCode(), HttpStatus.BAD_REQUEST);

        verify(iotService).getDevice(productId, timestamp);
    }

    @Test
    public void testGetDeviceWithException() {
        String productId = "WG11155631";
        String timestamp = "1582605137000";

        when(iotService.getDevice(productId, timestamp)).thenThrow(new RuntimeException("runtime exception"));

        ResponseEntity<ResponseObject> actualResponse = (ResponseEntity<ResponseObject>) appController.getDevice(productId, timestamp);
        assertEquals(actualResponse.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);

        verify(iotService).getDevice(productId, timestamp);
    }
}
