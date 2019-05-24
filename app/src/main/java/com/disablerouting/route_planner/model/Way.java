package com.disablerouting.route_planner.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Way {

    @JsonProperty("nd")
    private List<Nd> mNdList;

    @JsonProperty("id")
    private String mID;

    @JsonProperty("version")
    private String mVersion;

    @JsonProperty("tag")
    private List<Tag> mTagList;

    public Way() {
    }

    public List<Nd> getNdList() {
        return mNdList;
    }

    public String getID() {
        return mID;
    }

    public String getVersion() {
        return mVersion;
    }

    public List<Tag> getTagList() {
        return mTagList;
    }
}
