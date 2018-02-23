package com.disablerouting.geo_coding.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Geometry {

    @JsonProperty("type")
    private String mType;

    @JsonProperty("geometry")
    private List<Double> coordinates = null;

    public String getType() {
        return mType;
    }

    public List<Double> getCoordinates() {
        return coordinates;
    }
}
