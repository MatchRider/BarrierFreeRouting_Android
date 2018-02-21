package com.disablerouting.route_planner;


import org.osmdroid.util.GeoPoint;

public interface OnSourceDestinationListener {

    void onGoClick(GeoPoint geoPointSource, GeoPoint geoPointDestination);

    void plotDataOnMap(String encodedString);

    void onBackPress();
}
