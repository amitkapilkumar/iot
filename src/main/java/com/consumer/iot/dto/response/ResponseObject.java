package com.consumer.iot.dto.response;

public class ResponseObject {
    private String description;

    public ResponseObject(final String description) {
        this.description = description;
    }

    public ResponseObject() { }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
