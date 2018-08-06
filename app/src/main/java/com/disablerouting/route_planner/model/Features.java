package com.disablerouting.route_planner.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Features {


    @JsonProperty("geometry")
    private Geometry mGeometry;

    @JsonProperty("id")
    private String mId;

    @JsonProperty("type")
    private String mType;

    @JsonProperty("properties")
    private Properties mProperties;

    public Geometry getGeometry() {
        return mGeometry;
    }

    public String getId() {
        return mId;
    }

    public String getType() {
        return mType;
    }

    public Properties getProperties() {
        return mProperties;
    }
}
