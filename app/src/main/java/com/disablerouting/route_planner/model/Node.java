package com.disablerouting.route_planner.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Node {

    @JsonProperty("id")
    private String mID;

    @JsonProperty("lon")
    private String mLongitude;

    @JsonProperty("lat")
    private String mLatitude;

    @JsonProperty("version")
    private String mVersion;

    @JsonProperty("tag")
    private List<Tag> mTag;

    public String getID() {
        return mID;
    }

    public String getLongitude() {
        return mLongitude;
    }

    public String getLatitude() {
        return mLatitude;
    }

    public String getVersion() {
        return mVersion;
    }

    public List<Tag> getTag() {
        return mTag;
    }


}
