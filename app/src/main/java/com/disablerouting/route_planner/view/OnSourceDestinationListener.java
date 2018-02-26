package com.disablerouting.route_planner.view;


import com.disablerouting.geo_coding.model.Features;
import org.osmdroid.util.GeoPoint;

public interface OnSourceDestinationListener {

    void onGoClick(GeoPoint geoPointSource, GeoPoint geoPointDestination);

    void plotDataOnMap(String encodedString);

    void onBackPress();

    void onGoSwapView(GeoPoint geoPointSource, GeoPoint geoPointDestination);

    void onSourceDestinationSelected(Features featuresSource, Features featuresDestination);
}
