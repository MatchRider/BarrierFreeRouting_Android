package com.disablerouting.route_planner.model;


import org.osmdroid.util.GeoPoint;

import java.util.List;

public class ProgressModel {

    private GeoPoint start;
    private WayCustomModel mWayCustomModel;
    private List<GeoPoint> mGeoPointList;
    private boolean mValid;

    public ProgressModel(List<GeoPoint> geoPoints, GeoPoint startPoint, WayCustomModel wayCustomModel, boolean valid) {
        this.start=startPoint;
        this.mWayCustomModel=wayCustomModel;
        this.mGeoPointList=geoPoints;
        this.mValid = valid;
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

    public boolean isValid() {
        return mValid;
    }
}
