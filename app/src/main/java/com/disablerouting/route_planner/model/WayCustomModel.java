package com.disablerouting.route_planner.model;


import org.osmdroid.util.GeoPoint;

import java.util.List;

public class WayCustomModel {

    private String mId;
    private List<GeoPoint> mGeoPoint;
    private String mColor;


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

    public String getColor() {
        return mColor;
    }

    public void setColor(String color) {
        this.mColor = color;
    }
}
