package com.disablerouting.capture_option.model;

import com.disablerouting.feedback.model.RequestTag;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name="node", strict=false)
public class Node {

    public Node(String changeset, String lat, String lon) {
        mChangeset = changeset;
        mLat = lat;
        mLon = lon;
    }

    @Attribute(name="changeset")
    private String mChangeset;

    @Attribute(name="lat")
    private String mLat;

    @Attribute(name="lon")
    private String mLon;

    @ElementList(name = "tag", inline=true,required=false)
    private List<RequestTag> mRequestTagList;


    public void setRequestTagList(List<RequestTag> requestTagList) {
        mRequestTagList = requestTagList;
    }

}
