package com.disablerouting.route_planner.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NodeType {

    @JsonProperty("id")
    private
    int mID;

    @JsonProperty("identifier")
    private
    String mIdentifier;

    public int getID() {
        return mID;
    }

    public String getIdentifier() {
        return mIdentifier;
    }
}
