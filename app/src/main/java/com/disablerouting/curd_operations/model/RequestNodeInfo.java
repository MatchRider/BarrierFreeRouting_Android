package com.disablerouting.curd_operations.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestNodeInfo {

    @JsonProperty("NodeData")
    private
    NodeReference mNodeReference;

    @JsonProperty("ModifiedByUser")
    private
    String mModifiedByUser;

    public RequestNodeInfo() {
    }

    public void setNodeReference(NodeReference nodeReference) {
        mNodeReference = nodeReference;
    }

    public void setModifiedByUser(String modifiedByUser) {
        mModifiedByUser = modifiedByUser;
    }
}
