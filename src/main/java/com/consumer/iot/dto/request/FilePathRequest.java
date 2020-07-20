package com.consumer.iot.dto.request;

import java.io.Serializable;

public class FilePathRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private String filepath;

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }
}
