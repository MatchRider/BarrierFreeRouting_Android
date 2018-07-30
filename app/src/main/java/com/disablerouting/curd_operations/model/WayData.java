package com.disablerouting.curd_operations.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WayData {

    @JsonProperty("Id")
    String mId;

    @JsonProperty("Attributes")
    List<Attributes> mAttributesList;

    public String getId() {
        return mId;
    }

    public List<Attributes> getAttributesList() {
        return mAttributesList;
    }
}
