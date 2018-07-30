package com.disablerouting.route_planner.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OSM {

    @JsonProperty("node")
    private List<Node> mNode;

    @JsonProperty("way")
    private List<Way> mWay;

    @JsonProperty("version")
    private float mVersion;

    @JsonProperty("generator")
    private String mGenerator;

    public List<Node> getNode() {
        return mNode;
    }

    public List<Way> getWay() {
        return mWay;
    }
}
