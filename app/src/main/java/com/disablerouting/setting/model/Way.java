package com.disablerouting.setting.model;


import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name="way", strict=false)
public class Way {

    @Attribute(name = "id", required = false)
    String mID;

    @Attribute(name = "changeset", required = false)
    String mChangeset;


    @Attribute(name = "versions", required = false)
    String mVersion;


    public Way(String ID, String changeset, String version, List<RequestTag> requestTag, List<RequestNode> requestNode) {
        mID = ID;
        mChangeset = changeset;
        mVersion = version;
        mRequestTag = requestTag;
        mRequestNode = requestNode;
    }

    @ElementList(name = "tag", required = false)
    private List<RequestTag> mRequestTag;

    @ElementList(name = "nd", required = false)
    private List<RequestNode> mRequestNode;


}
