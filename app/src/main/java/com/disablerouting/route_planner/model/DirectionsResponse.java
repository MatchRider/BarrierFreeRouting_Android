package com.disablerouting.route_planner.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DirectionsResponse {

    @JsonProperty("features")
    private List<Features> mFeaturesList;

    @JsonProperty("bbox")
    private List<Double> bbox = null;

    @JsonProperty("info")
    private Info mInfo;

    public Info getInfo() {
        return mInfo;
    }

    public List<Features> getFeaturesList() {
        return mFeaturesList;
    }

    public List<Double> getBbox() {
        return bbox;
    }
}
