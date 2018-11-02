package com.disablerouting.route_planner.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Nd {

    @JsonProperty("ref")
    private String mRef;

    public Nd() {
    }

    public String getRef() {
        return mRef;
    }
}
