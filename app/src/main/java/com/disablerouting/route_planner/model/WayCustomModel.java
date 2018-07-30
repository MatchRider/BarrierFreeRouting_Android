package com.disablerouting.route_planner.model;


import org.osmdroid.util.GeoPoint;

import java.util.List;

public class WayCustomModel {

    List<Node> mNode;
    String mId;
    List<GeoPoint> mGeoPoint;
    List<Tag> mTag;

    public List<Node> getNode() {
        return mNode;
    }

    public void setNode(List<Node> node) {
        mNode = node;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public List<GeoPoint> getGeoPoint() {
        return mGeoPoint;
    }

    public void setGeoPoint(List<GeoPoint> geoPoint) {
        mGeoPoint = geoPoint;
    }

    public List<Tag> getTag() {
        return mTag;
    }

    public void setTag(List<Tag> tag) {
        mTag = tag;
    }
}
