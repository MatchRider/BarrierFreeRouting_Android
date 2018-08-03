package com.disablerouting.curd_operations.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AttributesValidate {

    @JsonProperty("footway")
    String mFootWay;


    @JsonProperty("highway")
    String mHighWay;

    public String getFootWay() {
        return mFootWay;
    }

    public String getHighWay() {
        return mHighWay;
    }
}
