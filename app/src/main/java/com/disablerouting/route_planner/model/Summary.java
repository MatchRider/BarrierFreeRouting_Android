package com.disablerouting.route_planner.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Summary {

    @JsonProperty("distance")
    private double mDistance;

    @JsonProperty("duration")
    private int mDuration;

    public double getDistance() {
        return mDistance;
    }

    public int getDuration() {
        return mDuration;
    }
}
