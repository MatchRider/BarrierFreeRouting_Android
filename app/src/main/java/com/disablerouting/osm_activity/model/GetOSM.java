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

    @JsonProperty("way")
    private List<Way> mWays;

    private List<Node> mNodeForWays;

    public List<Node> getNodeForWays() {
        return mNodeForWays;
    }

    public void setmNodeForWays(List<Node> mNodeForWays) {
        this.mNodeForWays = mNodeForWays;
    }

    public List<Node> getNode() {
        return mNode;
    }

    public GetOSM() {
    }

    public List<Way> getWays() {
        return mWays;
    }

    public void setNode(List<Node> node) {
        mNode = node;
    }

    public void setWays(List<Way> ways) {
        mWays = ways;
    }
}
