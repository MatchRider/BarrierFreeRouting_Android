package com.disablerouting.route_planner.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Tag {

    @JsonProperty("v")
    private String mV;

    @JsonProperty("k")
    private String mK;

    public String getV() {
        return mV;
    }
    public String getK() {
        return mK;
    }
}
