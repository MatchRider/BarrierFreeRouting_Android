package com.disablerouting.route_planner.view;


import org.osmdroid.util.GeoPoint;

public interface OnSourceDestinationListener {

    void onGoClick(GeoPoint geoPointSource, GeoPoint geoPointDestination);

    void plotDataOnMap(String encodedString);

    void onBackPress();

    void onSourceCompleted(String addressModel);

    void onDestinationCompleted(String addressModel);

    void onGoSwapView(GeoPoint geoPointSource, GeoPoint geoPointDestination);


}
