package com.disablerouting.route_planner.model;


import org.osmdroid.util.GeoPoint;

import java.util.List;

public class ProgressModel {

    private GeoPoint start;
    private WayCustomModel mWayCustomModel;
    private List<GeoPoint> mGeoPointList;

    public ProgressModel(List<GeoPoint> geoPoints, GeoPoint startPoint, WayCustomModel wayCustomModel) {
        this.start=startPoint;
        this.mWayCustomModel=wayCustomModel;
        this.mGeoPointList=geoPoints;
    }

    public GeoPoint getStart() {
        return start;
    }

    public WayCustomModel getWayCustomModel() {
        return mWayCustomModel;
    }

    public List<GeoPoint> getGeoPointList() {
        return mGeoPointList;
    }
}
