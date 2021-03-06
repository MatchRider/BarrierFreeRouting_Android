package com.disablerouting.route_planner.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Summary {

    @JsonProperty("distance")
    private double mDistance;

    @JsonProperty("duration")
    private int mDuration;

    @JsonProperty("ascent")
    private double mAscent;

    @JsonProperty("descent")
    private double mDescent;

    public double getDistance() {
        return mDistance;
    }

    public int getDuration() {
        return mDuration;
    }

    public double getAscent() {
        return mAscent;
    }

    public double getDescent() {
        return mDescent;
    }
}
