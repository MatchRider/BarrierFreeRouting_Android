package com.disablerouting.route_planner.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GeoPointGeometry {

    double mLat;

    double mLon;

    double mAlt;

    public GeoPointGeometry() {
    }

    public double getLatitude() {
        return mLat;
    }

    public double getLongitude() {
        return mLon;
    }

    public double getAlt() {
        return mAlt;
    }
}
