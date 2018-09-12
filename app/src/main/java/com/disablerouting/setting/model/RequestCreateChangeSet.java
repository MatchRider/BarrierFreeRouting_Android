package com.disablerouting.setting.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="osm", strict=false)
public class RequestCreateChangeSet {

    public RequestCreateChangeSet() {
    }

    @Element(name="way")
    private Way mWay;

    public Way getWay() {
        return mWay;
    }

    public void setWay(Way way) {
        mWay = way;
    }
}
