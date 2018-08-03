package com.disablerouting.curd_operations.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestValidate {

    @JsonProperty("WayData")
    List<WayDataValidate> mWayDataValidates;

    public List<WayDataValidate> getWayDataValidates() {
        return mWayDataValidates;
    }

    public void setWayDataValidates(List<WayDataValidate> wayDataValidates) {
        mWayDataValidates = wayDataValidates;
    }
}
