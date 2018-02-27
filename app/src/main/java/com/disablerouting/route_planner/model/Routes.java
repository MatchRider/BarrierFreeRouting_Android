package com.disablerouting.route_planner.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Routes {

    @JsonProperty("summary")
    private Summary mSummary;

    @JsonProperty("geometry")
    private String mGeometry;

    public Summary getSummary() {
        return mSummary;
    }

    public String getGeometry() {
        return mGeometry;
    }
}
