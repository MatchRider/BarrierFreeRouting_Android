package com.disablerouting.geo_coding.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GeoCodingResponse {

    @JsonProperty("features")
    private List<Features> mFeatures;

    public List<Features> getFeatures() {
        return mFeatures;
    }
}
