package com.disablerouting.route_planner.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Way {

    @JsonProperty("nd")
    private List<Nd> mNdList;

    @JsonProperty("id")
    private String mID;

    @JsonProperty("tag")
    private List<Tag> mTagList;

    public List<Nd> getNdList() {
        return mNdList;
    }

    public String getID() {
        return mID;
    }

    public List<Tag> getTagList() {
        return mTagList;
    }
}
