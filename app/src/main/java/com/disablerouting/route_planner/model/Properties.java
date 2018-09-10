package com.disablerouting.route_planner.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Properties {

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    @JsonProperty("summary")
    private List<Summary> mSummary;

    @JsonProperty("segments")
    private List<Segment> mSegmentList;

    public List<Segment> getSegmentList() {
        return mSegmentList;
    }

    public List<Summary> getSummary() {
        return mSummary;
    }
}
