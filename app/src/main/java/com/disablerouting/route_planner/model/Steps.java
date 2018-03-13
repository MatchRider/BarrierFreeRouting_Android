package com.disablerouting.route_planner.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Steps {

    @JsonProperty("name")
    private String mName;

    @JsonProperty("way_points")
    private ArrayList<Integer> mDoublesWayPoints;

    public ArrayList<Integer> getDoublesWayPoints() {
        return mDoublesWayPoints;
    }
}
