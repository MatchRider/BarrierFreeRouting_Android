package com.disablerouting.route_planner.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Routes {

    @JsonProperty("summary")
    private Summary mSummary;

    @JsonProperty("geometry")
    private String mGeometry;

    @JsonProperty("segments")
    private List<Segment> mSegmentList;

    public List<Segment> getSegmentList() {
        return mSegmentList;
    }

    public Summary getSummary() {
        return mSummary;
    }

    public String getGeometry() {
        return mGeometry;
    }
}
