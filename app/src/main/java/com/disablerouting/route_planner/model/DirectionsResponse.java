package com.disablerouting.route_planner.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DirectionsResponse {

    @JsonProperty("routes")
    private List<Routes> mRoutesList;

    public List<Routes> getRoutesList() {
        return mRoutesList;
    }
}
