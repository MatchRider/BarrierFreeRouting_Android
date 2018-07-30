package com.disablerouting.curd_operations.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Attributes {

    @JsonProperty("Key")
    String mKey;


    @JsonProperty("Value")
    String mValue;

    @JsonProperty("IsValid")
    boolean mIsValid;

    public String getKey() {
        return mKey;
    }

    public String getValue() {
        return mValue;
    }

    public boolean isValid() {
        return mIsValid;
    }
}
