package com.disablerouting.feedback.model;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name="osm", strict=false)
public class RequestCreateChangeSet {

    public RequestCreateChangeSet() {
    }

    @ElementList(name="tag",inline=true)
    @Path("changeset")
    private List<RequestTag> mRequestTag;

    public List<RequestTag> getRequestTag() {
        return mRequestTag;
    }

    public void setRequestTag(List<RequestTag> requestTag) {
        mRequestTag = requestTag;
    }
}
