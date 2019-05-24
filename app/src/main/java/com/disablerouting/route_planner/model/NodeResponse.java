package com.disablerouting.route_planner.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NodeResponse {

    @JsonProperty("nodes")
    private
    List<NodeItem> mNodes;

    public List<NodeItem> getNodes() {
        return mNodes;
    }
}
