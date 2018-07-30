package com.disablerouting.curd_operations.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseWay {


    @JsonProperty("WayData")
    List<WayData> mWayData;

    @JsonProperty("Status")
    boolean mStatus;

    @JsonProperty("Error")
    String mError;

    public List<WayData> getWayData() {
        return mWayData;
    }

    public boolean isStatus() {
        return mStatus;
    }

    public String getError() {
        return mError;
    }
}
