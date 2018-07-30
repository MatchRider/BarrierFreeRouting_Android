package com.disablerouting.curd_operations.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestGetWay {

    @JsonProperty("WayId")
    String mStringWay;

    public void setStringWay(String stringWay) {
        mStringWay = stringWay;
    }
}
