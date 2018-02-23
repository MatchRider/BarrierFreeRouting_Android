package com.disablerouting.geo_coding.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Features {

    @JsonProperty("type")
    private String mType;

    @JsonProperty("geometry")
    private Geometry mGeometry ;

    @JsonProperty("properties")
    private Properties mProperties;

    public String getType() {
        return mType;
    }

    public Geometry getGeometry() {
        return mGeometry;
    }

    public Properties getProperties() {
        return mProperties;
    }
}
