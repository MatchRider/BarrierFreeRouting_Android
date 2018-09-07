package com.disablerouting.curd_operations.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestWayData {

    @JsonProperty("Id")
    private
    String mId;

    @JsonProperty("ProjectId")
    private
    String mProjectId;

    @JsonProperty("Version")
    private
    String mVersion;

    @JsonProperty("IsValid")
    private
    String mIsValid;

    @JsonProperty("Attributes")
    private
    List<AttributesValidate> mAttributesValidate;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getProjectId() {
        return mProjectId;
    }

    public void setProjectId(String projectId) {
        mProjectId = projectId;
    }

    public String isValid() {
        return mIsValid;
    }

    public void setValid(String valid) {
        mIsValid = valid;
    }

    public String getIsValid() {
        return mIsValid;
    }

    public void setIsValid(String isValid) {
        mIsValid = isValid;
    }

    public List<AttributesValidate> getAttributesValidate() {
        return mAttributesValidate;
    }

    public void setAttributesValidate(List<AttributesValidate> attributesValidate) {
        mAttributesValidate = attributesValidate;
    }

    public String getVersion() {
        return mVersion;
    }

    public void setVersion(String version) {
        mVersion = version;
    }
}
