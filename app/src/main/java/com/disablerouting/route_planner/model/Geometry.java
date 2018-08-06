package com.disablerouting.route_planner.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Geometry {

    @JsonProperty("coordinates")
    private List<List<Double>> coordinates = null;

    @JsonProperty("type")
    private String type;

    public List<List<Double>> getCoordinates() {
        return coordinates;
    }

    public String getType() {
        return type;
    }
}
