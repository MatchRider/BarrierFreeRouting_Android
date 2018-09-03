package com.disablerouting.curd_operations.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AttributesValidate {

    @JsonProperty("Key")
    private
    String mKey;

    @JsonProperty("Value")
    private
    String mValue;

    @JsonProperty("IsValid")
    private
    boolean mIsValid;

    public String getKey() {
        return mKey;
    }

    public void setKey(String key) {
        mKey = key;
    }

    public String getValue() {
        return mValue;
    }

    public void setValue(String value) {
        mValue = value;
    }

    public boolean isValid() {
        return mIsValid;
    }

    public void setValid(boolean valid) {
        mIsValid = valid;
    }
}
