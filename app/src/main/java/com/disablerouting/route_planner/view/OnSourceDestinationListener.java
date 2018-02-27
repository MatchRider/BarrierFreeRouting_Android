package com.disablerouting.route_planner.view;


import com.disablerouting.geo_coding.model.Features;

public interface OnSourceDestinationListener {

    void plotDataOnMap(String encodedString);

    void onBackPress();

    void onSourceDestinationSelected(Features featuresSource, Features featuresDestination);
}
