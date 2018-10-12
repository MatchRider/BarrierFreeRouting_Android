package com.disablerouting.osm_activity.model;

import com.disablerouting.route_planner.model.Node;
import com.disablerouting.route_planner.model.Way;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GetOSM {

    @JsonProperty("node")
    private List<Node> mNode;

    public List<Node> getNode() {
        return mNode;
    }

    @JsonProperty("way")
    private List<com.disablerouting.route_planner.model.Way> mWays;

    public List<Way> getWays() {
        return mWays;
    }
}
