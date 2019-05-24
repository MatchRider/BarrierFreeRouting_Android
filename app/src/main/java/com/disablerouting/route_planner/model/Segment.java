package com.disablerouting.route_planner.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
public class Segment {

    @JsonProperty("distance")
    private double mDistance;

    @JsonProperty("duration")
    private double mDuration;

    @JsonProperty("steps")
    private List<Steps> mStepsList;

    public double getDistance() {
        return mDistance;
    }

    public double getDuration() {
        return mDuration;
    }

    public List<Steps> getStepsList() {
        return mStepsList;
    }
}
