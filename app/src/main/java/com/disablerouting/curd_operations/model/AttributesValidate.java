package com.disablerouting.curd_operations.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AttributesValidate {

    @JsonProperty("footway")
    String mFootWay;


    @JsonProperty("highway")
    String mHighWay;


    @JsonProperty("incline")
    String mIncline;

    @JsonProperty("width")
    String mHWidth;


    public String getFootWay() {
        return mFootWay;
    }

    public String getHighWay() {
        return mHighWay;
    }

    public void setFootWay(String footWay) {
        mFootWay = footWay;
    }

    public void setHighWay(String highWay) {
        mHighWay = highWay;
    }

    public String getIncline() {
        return mIncline;
    }

    public void setIncline(String incline) {
        mIncline = incline;
    }

    public String getHWidth() {
        return mHWidth;
    }

    public void setHWidth(String HWidth) {
        mHWidth = HWidth;
    }
}
