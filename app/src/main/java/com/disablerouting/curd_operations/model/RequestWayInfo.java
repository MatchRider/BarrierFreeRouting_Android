package com.disablerouting.curd_operations.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestWayInfo {

    @JsonProperty("WayData")
    private
    RequestWayData mWayDataValidates;

    public RequestWayData getWayDataValidates() {
        return mWayDataValidates;
    }

    public void setWayDataValidates(RequestWayData wayDataValidates) {
        mWayDataValidates = wayDataValidates;
    }
}
