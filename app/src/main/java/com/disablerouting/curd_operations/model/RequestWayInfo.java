package com.disablerouting.curd_operations.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestWayInfo {

    @JsonProperty("WayData")
    private
    RequestWayData mWayDataValidates;

    @JsonProperty("ModifiedByUser")
    private
    String mModifiedByUser;

    public RequestWayInfo() {
    }

    public void setWayDataValidates(RequestWayData wayDataValidates) {
        mWayDataValidates = wayDataValidates;
    }

    public void setModifiedByUser(String modifiedByUser) {
        mModifiedByUser = modifiedByUser;
    }
}
