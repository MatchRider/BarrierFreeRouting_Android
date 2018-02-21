package com.disablerouting.route_planner.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Routes {

    @JsonProperty("geometry")
    private String mGeometry;

    public String getGeometry() {
        return mGeometry;
    }
}
