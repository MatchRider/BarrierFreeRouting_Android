package com.disablerouting.osm_activity.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GetOsmData {

    @JsonProperty("osm")
    private GetOSM mOSM;

    public GetOSM getOSM() {
        return mOSM;
    }
    public GetOsmData() {
    }

    public void setOSM(GetOSM OSM) {
        mOSM = OSM;
    }
}
