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

    public void setType(String type) {
        mType = type;
    }

    public void setGeometry(Geometry geometry) {
        mGeometry = geometry;
    }

    public void setProperties(Properties properties) {
        mProperties = properties;
    }

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
