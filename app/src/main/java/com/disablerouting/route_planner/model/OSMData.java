package com.disablerouting.route_planner.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OSMData {

    @JsonProperty("osm")
    private OSM mOSM;

    public OSM getOSM() {
        return mOSM;
    }
}
