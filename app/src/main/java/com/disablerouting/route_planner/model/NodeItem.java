package com.disablerouting.route_planner.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NodeItem {

    @JsonProperty("name")
    private
    String mName;

    @JsonProperty("wheelchair")
    private
    String mWheelChair;

    @JsonProperty("wheelchair_toilet")
    private
    String mWheelChairToilet;

    @JsonProperty("lat")
    private double mLatitude;

    @JsonProperty("lon")
    private double mLongitude;

    @JsonProperty("node_type")
    private
    NodeType mNodeType;

    @JsonProperty("category")
    private
    Category mCategory;

    public String getName() {
        return mName;
    }

    public double getLatitude() {
        return mLatitude;
    }


    public double getLongitude() {
        return mLongitude;
    }

    public Category getCategory() {
        return mCategory;
    }

    public NodeType getNodeType() {
        return mNodeType;
    }

    public String getWheelChair() {
        return mWheelChair;
    }
}
