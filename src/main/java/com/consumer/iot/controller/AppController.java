package com.consumer.iot.controller;

import com.consumer.iot.dto.request.FilePathRequest;
import com.consumer.iot.dto.response.DeviceResponse;
import com.consumer.iot.dto.response.ResponseObject;
import com.consumer.iot.exception.DeviceNotFoundException;
import com.consumer.iot.exception.DeviceNotLocatedException;
import com.consumer.iot.service.IotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class AppController {

    @Autowired
    private IotService iotService;

    private static final String DATA_REFRESHED = "Data refreshed ..";
    private static final String TECHNICAL_EXCEPTION = "ERROR: A technical exception occurred";
    private static final String NO_DATA_FOUND = "ERROR: no data file found";

    @PostMapping(path = "/iot/event/v1", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ResponseObject> loadDataFromFile(@RequestBody FilePathRequest request) {
        try {
            if(iotService.loadData(request.getFilepath())) {
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(DATA_REFRESHED));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(NO_DATA_FOUND));
        }
        catch (Exception e) {
            String exception = TECHNICAL_EXCEPTION + " " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObject(exception));
        }
    }

    @GetMapping("/iot/event/v1")
    public ResponseEntity<?> getDevice(@RequestParam("ProductId") String productId, @RequestParam(value = "tstmp", required = false) String timestamp) {
        try {
            DeviceResponse response = iotService.getDevice(productId, timestamp);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        catch (DeviceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject("ERROR: Id "+ productId + " not found"));
        }
        catch (DeviceNotLocatedException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject("ERROR: Device could not be located"));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObject("ERROR: Internal Server Error"));
        }
    }
}
