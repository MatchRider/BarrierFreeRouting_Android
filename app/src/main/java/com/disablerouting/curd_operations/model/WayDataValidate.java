package com.disablerouting.curd_operations.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WayDataValidate {

    @JsonProperty("Id")
    String mId;

    @JsonProperty("Attributes")
    AttributesValidate mAttributesValidate;

    public String getId() {
        return mId;
    }

    public AttributesValidate getAttributes() {
        return mAttributesValidate;
    }
}
